package com.racloop.staticdata



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional


class StaticDataController {

    //static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [staticDataInstanceList: StaticData.list(params), staticDataInstanceTotal: StaticData.count()]
    }

    def create() {
        [staticDataInstance: new StaticData(params)]
    }

    def save() {
        def staticDataInstance = new StaticData(params)
        if (!staticDataInstance.save(flush: true)) {
            render(view: "create", model: [staticDataInstance: staticDataInstance])
            return
        }

        flash.message = "Page ${staticDataInstance?.key} updated successfully"
        redirect(action: "show", id: staticDataInstance.id)
    }

    def show(Long id) {
        def staticDataInstance = StaticData.get(id)
        if (!staticDataInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'staticData.label', default: 'StaticData'), id])
            redirect(action: "list")
            return
        }

        [staticDataInstance: staticDataInstance]
    }

    def edit(Long id) {
        def staticDataInstance = StaticData.get(id)
        if (!staticDataInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'staticData.label', default: 'StaticData'), id])
            redirect(action: "list")
            return
        }

        [staticDataInstance: staticDataInstance]
    }

    def update(Long id, Long version) {
        def staticDataInstance = StaticData.get(id)
        if (!staticDataInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'staticData.label', default: 'StaticData'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (staticDataInstance.version > version) {
                staticDataInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'staticData.label', default: 'StaticData')] as Object[],
                          "Another user has updated this StaticData while you were editing")
                render(view: "edit", model: [staticDataInstance: staticDataInstance])
                return
            }
        }

        staticDataInstance.properties = params

        if (!staticDataInstance.save(flush: true)) {
            render(view: "edit", model: [staticDataInstance: staticDataInstance])
            return
        }

        flash.message = "Page <strong>${staticDataInstance?.staticDataKey}</strong> updated successfully"
        redirect(action: "show", id: staticDataInstance.id)
    }
}
