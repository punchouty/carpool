package com.racloop.staticdata



import grails.test.mixin.*
import spock.lang.*

@TestFor(StaticDataController)
@Mock(StaticData)
class StaticDataControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.staticDataInstanceList
            model.staticDataInstanceCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.staticDataInstance!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            def staticData = new StaticData()
            staticData.validate()
            controller.save(staticData)

        then:"The create view is rendered again with the correct model"
            model.staticDataInstance!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            staticData = new StaticData(params)

            controller.save(staticData)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/staticData/show/1'
            controller.flash.message != null
            StaticData.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def staticData = new StaticData(params)
            controller.show(staticData)

        then:"A model is populated containing the domain instance"
            model.staticDataInstance == staticData
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def staticData = new StaticData(params)
            controller.edit(staticData)

        then:"A model is populated containing the domain instance"
            model.staticDataInstance == staticData
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/staticData/index'
            flash.message != null


        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def staticData = new StaticData()
            staticData.validate()
            controller.update(staticData)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.staticDataInstance == staticData

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            staticData = new StaticData(params).save(flush: true)
            controller.update(staticData)

        then:"A redirect is issues to the show action"
            response.redirectedUrl == "/staticData/show/$staticData.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/staticData/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def staticData = new StaticData(params).save(flush: true)

        then:"It exists"
            StaticData.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(staticData)

        then:"The instance is deleted"
            StaticData.count() == 0
            response.redirectedUrl == '/staticData/index'
            flash.message != null
    }
}
