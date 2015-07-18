package racloop



class ArchiveJob {
	def userReviewService
	static triggers = {
		cron name: 'userFeedback', cronExpression: "0 0 6 * * ?"
	}

    def execute() {
		log.info "Starting Archive job"
        userReviewService.markUsersForPendingReview()
		log.info "Sucessfully completed Archive job"
    }
}
