package racloop



class HouseKeepingJob {
	def houseKeepingService
	

	static triggers = {
		cron name: 'houseKeepingJob', cronExpression: "0 0 7 * * ?"
	}

	def execute() {
		log.info "Starting houseKeeping job"
		houseKeepingService.archiveAnalyticsLogs()
		log.info "Sucessfully completed houseKeeping job"
	}
}
