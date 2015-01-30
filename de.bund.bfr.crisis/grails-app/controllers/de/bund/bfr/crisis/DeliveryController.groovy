package de.bund.bfr.crisis



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class DeliveryController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Delivery.list(params), model:[deliveryInstanceCount: Delivery.count()]
    }

    def show(Delivery deliveryInstance) {
        respond deliveryInstance
    }

    def create() {
        respond new Delivery(params)
    }

    @Transactional
    def save(Delivery deliveryInstance) {
        if (deliveryInstance == null) {
            notFound()
            return
        }

        if (deliveryInstance.hasErrors()) {
            respond deliveryInstance.errors, view:'create'
            return
        }

        deliveryInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'delivery.label', default: 'Delivery'), deliveryInstance.id])
                redirect deliveryInstance
            }
            '*' { respond deliveryInstance, [status: CREATED] }
        }
    }

    def edit(Delivery deliveryInstance) {
        respond deliveryInstance
    }

    @Transactional
    def update(Delivery deliveryInstance) {
        if (deliveryInstance == null) {
            notFound()
            return
        }

        if (deliveryInstance.hasErrors()) {
            respond deliveryInstance.errors, view:'edit'
            return
        }

        deliveryInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Delivery.label', default: 'Delivery'), deliveryInstance.id])
                redirect deliveryInstance
            }
            '*'{ respond deliveryInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Delivery deliveryInstance) {

        if (deliveryInstance == null) {
            notFound()
            return
        }

        deliveryInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Delivery.label', default: 'Delivery'), deliveryInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'delivery.label', default: 'Delivery'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
