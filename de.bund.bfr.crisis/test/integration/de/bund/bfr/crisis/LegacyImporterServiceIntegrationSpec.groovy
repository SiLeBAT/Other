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
		legacyImporterService.bulkImportFromHSQLFile(/C:\Users\heisea\Material\SA_1.8.2.0.0_20141202_EHEC\DB/)
			
		then:
		Product.count() == 148	
    }
}
