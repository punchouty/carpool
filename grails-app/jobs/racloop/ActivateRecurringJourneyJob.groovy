package racloop



class ActivateRecurringJourneyJob {
	
	def recurrenceJourneyService
	def staticdataService
	
    static triggers = {
      simple name: 'ActivateRecurring', startDelay: 1800000, repeatInterval: 1*60*60*1000
    }

    def execute() {
        if (staticdataService.canRunJob()) {
			log.info "Starting ActivateRecurringJourney job"
			recurrenceJourneyService.activateJourneys()
			log.info "Sucessfully completed ActivateRecurringJourney job"
		}
		else {
			log.info "Not running ActivateRecurringJourney job as I am not configured to run on the machine"
		}
    }
}
