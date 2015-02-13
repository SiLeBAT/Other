package de.bund.bfr.crisis

import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;

import grails.transaction.Transactional
import grails.converters.*

//@Transactional(readOnly = true)
class MapService {
	//	boolean transactional = true
	GrailsApplication grailsApplication

	static expose = ["gwt:de.bund.bfr.crisis.client"]
	
	String search(String searchString) {
		List<Station> stations
		List<Delivery> deliveries
		// for empty search string, return all
		if(!searchString) {
			stations = Station.all
			deliveries = Delivery.findAllByRecipientIsNotNull()
		}
		else {
			// find all stations containing the string (translates into SQL LIKE)
			stations = Station.findAllByNameIlike("%$searchString%")
			List<Delivery> outgoingDeliveries = stations*.products*.lots*.deliveries.flatten().findAll { it.recipient }
			
			def potentiallyIncomingLots = stations*.products*.lots*.id.flatten()
			List<Delivery> incomingDeliveries = FoodRecipe.findAll {
				lot.id in potentiallyIncomingLots
			}*.ingredient
			
			deliveries = (outgoingDeliveries + incomingDeliveries).unique { it.id } 

			// add also all stations relevant to the found deliveries
			deliveries.each { delivery ->
				stations.add(delivery.station)
				if(delivery.recipient) stations.add(delivery.recipient)
			}

			// make sure every station is only returned once
			stations.unique(true, { it.id })
		}
		
		[stations: stations.collect { collectProperties(it, 'id', 'name', 'longitude', 'latitude') }, 
		 deliveries: deliveries.collect { delivery -> collectProperties(delivery, 'id', 'station', 'recipient') }] as JSON
	}
	
	private Map<String, Object> collectProperties(object, ... properties) {
		properties.collectEntries { propertyName ->
			def value = object."$propertyName"
				// take only ID for domain classes				
			if(value && GrailsUtil.isDomainClass(grailsApplication, value.class)) 
				value = value.id
			[(propertyName): value] 
		}
	}

	String searchSuggestions(String searchString) {
		// either return all, nothing, or LRU
		if(!searchString)
			return []

		List<Station> stations = Station.findAllByNameIlike("%$searchString%")
		stations*.name.unique() as JSON
	}
	
	String getStationId(String searchString) {
		// either return all, nothing, or LRU
		if(!searchString)
			return []

		Station station = Station.findByName(searchString)
		station?.id as JSON // station==null?null:station.id
	}
}
