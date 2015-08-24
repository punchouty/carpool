package racloop



class AutoMatcherJob {
	
	def autoMatcherService
	def staticdataService
	
    static triggers = {
      simple name: 'autoMatcher', startDelay: 1200000, repeatInterval: 12*60*60*1000
    }

    def execute() {
        if (staticdataService.canRunJob()) {
			log.info "Starting AutoMatcher job"
			autoMatcherService.sendAutoMatchNotificaiton()
			log.info "Sucessfully completed AutoMatcher job"
		}
		else {
			log.info "Not running AutoMatcher job as I am not configured to run on the machine"
		}
    }
}
