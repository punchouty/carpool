package com.racloop

import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

class SampleDataController {
	
	def sampleDataService

    def index() { 
		render "Empty Implementation"
	}
	
	def delete() {		
		Environment.executeForCurrentEnvironment {
			development {
				sampleDataService.deleteSampleData();
				render "Delete Complete"
			}
			test {
				render "Empty Implementation"
			}
			production {
				render "Empty Implementation"
			}
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
}
