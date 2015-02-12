import grails.util.Environment
import de.bund.bfr.LegacyImporterService
import de.bund.bfr.crisis.Delivery
import de.bund.bfr.crisis.FoodRecipe
import de.bund.bfr.crisis.Lot
import de.bund.bfr.crisis.Product
import de.bund.bfr.crisis.Station
import de.bund.bfr.crisis.User


class BootStrap {
	LegacyImporterService legacyImporterService

    def init = { servletContext ->
		if (Environment.current == Environment.DEVELOPMENT) {
//			legacyImporterService.bulkImportFromHSQLFile('test/resources/importTest/DB')
//			legacyImporterService.bulkImportFromHSQLFile('test/resources/importDB/EHEC/DB')
			new User(name: 'Dr. Armin Weiser', email: 'armin.weiser@bfr.bund.de').save()
			new User(name: 'Arvid Heise', email: 'arvid.heise@bfr.bund.de').save()
			
			def bfr1 = new Station(name: 'Bundesinstitut für Risikobewertung (Marienfelde)', 
				latitude: '52.409909' as BigDecimal, longitude: '13.364732' as BigDecimal,
				vatNumber: 'vat1', country: 'Germany', county: 'Berlin', street: 'Diedersdorfer Str')
			def bvl = new Station(name: 'Bundesinstitut für gesundheitlichen Verbraucherschutz und Veterinärmedizin', 
				latitude: '52.443859' as BigDecimal, longitude: '13.283633' as BigDecimal,
				vatNumber: 'vat2', country: 'Germany', county: 'Berlin')
			def bfr2 = new Station(name: 'Bundesinstitut für Risikobewertung (Jungfernheide)', 
				latitude: '52.531075' as BigDecimal, longitude: '13.296178' as BigDecimal,
				vatNumber: 'vat3', country: 'Germany', county: 'Berlin')
			
			Random rand = new Random()
			[bfr1, bvl, bfr2].each { Station station ->
				['Salmonella', 'Campylobacter'].each { bacci ->
					def randomAmount = rand.nextInt(9) + 1
					def lot = new Lot(amount: randomAmount, unit: 'kg').
						addToDeliveries(new Delivery(amount: randomAmount * 1 / 4, unit: 'kg')).
						addToDeliveries(new Delivery(amount: randomAmount * 3 / 4, unit: 'kg')).
						save(failOnError: true)
					def product = new Product(denomination: bacci).
						addToLots(lot).
						save(failOnError: true)
					station.
						addToProducts(product).
						save(failOnError: true)
				}
			}
			
			[(bvl): 'Salmonella', (bfr2): 'Campylobacter'].each { Station recipient, String productName ->
				def delivery = bfr1.products.find { it.denomination == productName }.lots[0].deliveries[0]
				def receivingLot = recipient.products.find { it.denomination == productName }.lots[0]
				def recipe = new FoodRecipe(ingredient: delivery, mixRatio: 0.5, lot: receivingLot)
				delivery.recipient = receivingLot.product.station
				delivery.
					addToFoodRecipes(recipe).
					save(failOnError: true)
				receivingLot.
					addToFoodRecipes(recipe).
					save(failOnError: true)
			}
		}
    }
    def destroy = {
    }
}
