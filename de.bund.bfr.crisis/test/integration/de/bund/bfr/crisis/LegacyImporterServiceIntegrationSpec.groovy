package de.bund.bfr.crisis

import grails.test.spock.IntegrationSpec
import de.bund.bfr.LegacyImporterService

class LegacyImporterServiceIntegrationSpec extends IntegrationSpec {
	LegacyImporterService legacyImporterService
	
    def setup() {
    }

    def cleanup() {
    }

    void "ehec import"() {
		when:
		legacyImporterService.bulkImportFromHSQLFile('test/resources/importTest/DB')
			
		then:
		Station.count() == 476	
		Product.count() == 176
		Lot.count() == 485	
		Delivery.count() == 2152	
		FoodRecipe.count() == 804	
    }
}
