package de.bund.bfr.crisis



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class StationController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Station.list(params), model:[stationInstanceCount: Station.count()]
    }

    def show(Station stationInstance) {
        respond stationInstance
    }

    def create() {
        respond new Station(params)
    }

    @Transactional
    def save(Station stationInstance) {
        if (stationInstance == null) {
            notFound()
            return
        }

        if (stationInstance.hasErrors()) {
            respond stationInstance.errors, view:'create'
            return
        }

        stationInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'station.label', default: 'Station'), stationInstance.id])
                redirect stationInstance
            }
            '*' { respond stationInstance, [status: CREATED] }
        }
    }

    def edit(Station stationInstance) {
        respond stationInstance
    }

    @Transactional
    def update(Station stationInstance) {
        if (stationInstance == null) {
            notFound()
            return
        }

        if (stationInstance.hasErrors()) {
            respond stationInstance.errors, view:'edit'
            return
        }

        stationInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Station.label', default: 'Station'), stationInstance.id])
                redirect stationInstance
            }
            '*'{ respond stationInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Station stationInstance) {

        if (stationInstance == null) {
            notFound()
            return
        }

        stationInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Station.label', default: 'Station'), stationInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'station.label', default: 'Station'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
