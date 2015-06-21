package racloop



class ArchiveJob {
	def userReviewService
	static triggers = {
		cron name: 'userFeedback', cronExpression: "0 0 6 * * ?"
	}

    def execute() {
        userReviewService.markUsersForPendingReview()
    }
}
