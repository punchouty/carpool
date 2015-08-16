package racloop



class ActivateRecurringJourneyJob {
	
	def autoMatcherService
	def staticdataService
	
    static triggers = {
      simple name: 'ActivateRecurring', startDelay: 150000, repeatInterval: 18*60*60*1000
    }

    def execute() {
        if (staticdataService.canRunJob()) {
			log.info "Starting ActivateRecurringJourney job"
			autoMatcherService.sendAutoMatchNotificaiton()
			log.info "Sucessfully completed ActivateRecurringJourney job"
		}
		else {
			log.info "Not running ActivateRecurringJourney job as I am not configured to run on the machine"
		}
    }
}
