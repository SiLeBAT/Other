package de.bund.bfr.crisis

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Delivery)
class DeliverySpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "should allow empty fields"() {
		new Delivery(amount: 10, unit: 'kg').save()
    }
}
