package com.racloop.staticdata



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class StaticDataController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond StaticData.list(params), model:[staticDataInstanceCount: StaticData.count()]
    }

    def show(StaticData staticDataInstance) {
        respond staticDataInstance
    }

    def create() {
        respond new StaticData(params)
    }

    @Transactional
    def save(StaticData staticDataInstance) {
        if (staticDataInstance == null) {
            notFound()
            return
        }

        if (staticDataInstance.hasErrors()) {
            respond staticDataInstance.errors, view:'create'
            return
        }

        staticDataInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'staticDataInstance.label', default: 'StaticData'), staticDataInstance.id])
                redirect staticDataInstance
            }
            '*' { respond staticDataInstance, [status: CREATED] }
        }
    }

    def edit(StaticData staticDataInstance) {
        respond staticDataInstance
    }

    @Transactional
    def update(StaticData staticDataInstance) {
        if (staticDataInstance == null) {
            notFound()
            return
        }

        if (staticDataInstance.hasErrors()) {
            respond staticDataInstance.errors, view:'edit'
            return
        }

        staticDataInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'StaticData.label', default: 'StaticData'), staticDataInstance.id])
                redirect staticDataInstance
            }
            '*'{ respond staticDataInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(StaticData staticDataInstance) {

        if (staticDataInstance == null) {
            notFound()
            return
        }

        staticDataInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'StaticData.label', default: 'StaticData'), staticDataInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'staticDataInstance.label', default: 'StaticData'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
