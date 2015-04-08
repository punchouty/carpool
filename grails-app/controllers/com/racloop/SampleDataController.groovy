package com.racloop

import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

class SampleDataController {
	
	def sampleDataService
	def smsService
	def grailsApplication

    def index() { 
		render "Empty Implementation"
	}
	
	def delete() {		
		Boolean deleteEnabled = grailsApplication.config.grails.enable.delete.all
		if(deleteEnabled) {
			def secret = params.secret
			if(secret.equals("cleanitup")) {
				sampleDataService.deleteSampleData();
				Journey.executeUpdate('delete from Journey');
				User.findAll ().each {
					it.activeJourneys.clear();
					it.save();
				}
				render "Delete Complete"
			}
			else {
				render "Wrong Password"
			}
		}
		else {
			render "Delete is disable like this"
		}		
	}
	
	def populate() {	
		Environment.executeForCurrentEnvironment {
			development {
				sampleDataService.populateSampleData();
				render "Population Complete"
			}
			test {
				render "Empty Implementation"
			}
			production {
				render "Empty Implementation"
			}
		}	
	}
	
	def sendSms() {
		def mobile = params.mobile
		def message = params.message
		boolean success = smsService.sendSms(mobile, message)
		if(success) {
			render "Success"
		}
		else {
			render "Fail"
		}
	}
}
