package racloop



class ArchiveJob {
	def userReviewService
	def staticdataService
	
	static triggers = {
		cron name: 'archiveJob', cronExpression: "0 0 6 * * ?"
	}

    def execute() {
		if (staticdataService.canRunJob()) {
			log.info "Starting Archive job"
			userReviewService.markUsersForPendingReview()
			log.info "Sucessfully completed Archive job"
		}
		else {
			log.info "Not running Archive job as I am not configured to run on the machine"
		}
		
    }
}
