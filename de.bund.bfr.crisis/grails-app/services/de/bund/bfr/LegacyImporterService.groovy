package de.bund.bfr

import grails.transaction.Transactional
import groovy.sql.Sql

import org.grails.datastore.gorm.GormValidationApi
import org.springframework.jdbc.datasource.DriverManagerDataSource

import de.bund.bfr.crisis.Delivery
import de.bund.bfr.crisis.FoodRecipe
import de.bund.bfr.crisis.Lot
import de.bund.bfr.crisis.Product
import de.bund.bfr.crisis.Serial
import de.bund.bfr.crisis.Station

@Transactional
class LegacyImporterService {
	def stationTranslation = [Name: 'name', Strasse: 'street', Hausnummer: 'houseNumber', Postfach: 'postOfficeBox',
		PLZ: 'zipCode', Ort: 'city', Bundesland: 'county', Land: 'country', Longitude: 'longitude', Latitude: 'latitude',
		Betriebsnummer: 'businessNumber', Betriebsart: 'typeOfBusiness', VATnumber: 'vatNumber', Kommentar: 'comment', ID: 'id']

	def productTranslation = [Artikelnummer: 'itemNumber', Bezeichnung: 'denomination', Prozessierung: 'processing',
		IntendedUse: 'intendedUse', Code: 'itemNumber', Kommentar: 'comment']

	def lotTranslation = [ChargenNr: 'lotNumber', Menge: 'amount', Einheit: 'unit', MHD_day: 'bestBeforeDay',
		MHD_month: 'bestBeforeMonth', MHD_year: 'bestBeforeYear', pd_day: 'productionDay',
		pd_month: 'productionMonth', pd_year: 'productionYear', OriginCountry: 'originCountry',
		microbioSample: 'examinationResult', Kommentar: 'comment']

	def deliveryTranslation = [dd_day: 'deliveryDateDay', dd_month: 'deliveryDateMonth', dd_year: 'deliveryDateYear',
		numPU: 'packagingUnits', typePU: 'packagingType', Unitmenge: 'amount', UnitEinheit: 'unit',
		EndChain: 'isEnd', Explanation_EndChain: 'endExplanation', Further_Traceback: 'furtherTraceback',
		Kommentar: 'comment']

	def bulkImport(String url, String user = 'SA', String password = '', String driver = 'org.hsqldb.jdbc.JDBCDriver') {
		def dataSource = new DriverManagerDataSource(url: url, username: user, password: password, driverClassName: driver)
		
		def sql = new Sql(dataSource)

		// Import stations
		Map<Integer, Station> legacyIDToStation = importStations(sql)

		Map<Integer, Product> legacyIDToProduct = importProducts(sql, legacyIDToStation)

		Map<Integer, Lot> legacyIDToLot = importLots(sql, legacyIDToProduct)

		Map<Integer, Delivery> legacyIDToDelivery = importDelivery(sql, legacyIDToLot, legacyIDToStation)

		List<GormValidationApi> newObjects = [legacyIDToStation, legacyIDToProduct, legacyIDToLot, legacyIDToDelivery]*.values().flatten()
		if(!newObjects*.validate(deepValidate: false).every()) {
			def erroneousObjects = newObjects.findAll { it.hasErrors() }
			def messages = erroneousObjects.collect { object ->
				object.errors
			}
			throw new IllegalArgumentException("Error importing datasets\n" + messages.join('\n'))
		}
		
		// update all stations with relations
		legacyIDToDelivery.values()*.save()
		legacyIDToStation.values()*.save()

		Map<Integer, FoodRecipe> legacyIDToRecipe = importRecipes(sql, legacyIDToLot, legacyIDToDelivery)
		
		// add recipes later to avoid cyclic dependencies
		legacyIDToRecipe.values()*.save()
	}

	Map<Integer, FoodRecipe> importRecipes(Sql sql, Map<Integer, Lot> legacyIDToLot, Map<Integer, Delivery> legacyIDToDelivery) {
		Map<Integer, FoodRecipe> legacyIDToRecipe = [:]		
		sql.eachRow('SELECT * from "ChargenVerbindungen" where "Produkt" IS NOT NULL') { legacyRecipe ->				
			def lot = legacyIDToLot[legacyRecipe.Produkt]
			def delivery = legacyIDToDelivery[legacyRecipe.Zutat]
			def recipe = new FoodRecipe(lot: lot, mixtureRatio: legacyRecipe.MixtureRatio)
			legacyIDToRecipe[legacyRecipe.ID] = recipe 
			lot?.addToFoodRecipes(recipe)
			delivery.addToFoodRecipes(recipe)
		}
		legacyIDToRecipe
	}

	private Map<Integer, Delivery> importDelivery(Sql sql, Map<Integer, Lot> legacyIDToLot, Map<Integer, Station> legacyIDToStation) {
		Map<Integer, Delivery> legacyIDToDelivery = [:]
		sql.eachRow('SELECT * from "Lieferungen"') { legacyDelivery ->
			// find an existing delivery if the ChargenNr contains at least one alphanumeric character or create a new one
			Delivery delivery = insertOrUpdate(legacyDelivery, null, Delivery, deliveryTranslation)
			if(legacyDelivery.'Empfänger' != null)
				delivery.recipient = legacyIDToStation[legacyDelivery.'Empfänger']
			def lot = legacyIDToLot[legacyDelivery.Charge]		
			lot?.addToDeliveries(delivery)
			legacyIDToDelivery[legacyDelivery.ID] = delivery			
		}
		return legacyIDToDelivery
	}

	private Map<Integer, Lot> importLots(Sql sql, Map<Integer, Product> legacyIDToProduct) {
		Map<Integer, Lot> legacyIDToLot = [:]
		sql.eachRow('SELECT * from "Chargen"') { legacyLot ->
			// find an existing lot if the ChargenNr contains at least one alphanumeric character or create a new one
			def product = legacyIDToProduct[legacyLot.Artikel]
			def lot = insertOrUpdate(legacyLot,
					legacyLot.ChargenNr =~ /\w/ ? product.lots.find { it.lotNumber == legacyLot.ChargenNr } : null,
					Lot, lotTranslation)
			product?.addToLots(lot)
			legacyIDToLot[legacyLot.ID] = lot
		}
		return legacyIDToLot
	}

	private Map<Integer, Product> importProducts(Sql sql, Map<Integer, Station> legacyIDToStation) {
		Map<Integer, Product> legacyIDToProduct = [:]
		sql.eachRow('SELECT * from "Produktkatalog"') { legacyProduct ->
			// find an existing product or create a new one
			def station = legacyIDToStation[legacyProduct.Station]
			def product = insertOrUpdate(legacyProduct,
					station.products.find { it.denomination == legacyProduct.Bezeichnung },
					Product, productTranslation)
			station?.addToProducts(product)
			legacyIDToProduct[legacyProduct.ID] = product
		}
		return legacyIDToProduct
	}

	def Map<Integer, Station> importStations(Sql sql) {
		Map<Integer, Station> legacyIDToStation = [:]
		sql.eachRow('SELECT * from "Station"') { legacyStation ->
			def station = insertOrUpdate(legacyStation, Station.findByName(legacyStation.Name),
					Station, stationTranslation)
			legacyIDToStation[legacyStation.ID] = station
		}
		return legacyIDToStation
	}

	def insertOrUpdate(legacyObject, existingObject, type, translation) {
		def object = existingObject
		if (!object) {
			object = type.newInstance()
			translation.each { from, to ->
				try {
					object."$to" = legacyObject."$from"
				} catch(e) {
//					log.error("Could not import field $from -> $to", e)
				}
			}
		} else {
			// check if we can fill some spots
			translation.each { from, to ->
				if(object."$to" == null)
					try {
						object."$to" = legacyObject."$from"
					} catch(e) {
//						log.error("Could not import field $from -> $to", e)
					}
			}
		}
		if(object.serial == null && legacyObject.serial != null)
			object.serial = makeSerial(legacyObject.serial)
		object
	}

	def makeSerial(String serial) {
		Serial.findBySerial(serial) ?: new Serial(serial)
	}

	def bulkImportFromHSQLFile(String path, String user = 'SA', String password = '') {
		bulkImport("jdbc:hsqldb:${new File(path).toURI().toURL()}", user, password)
	}
}
