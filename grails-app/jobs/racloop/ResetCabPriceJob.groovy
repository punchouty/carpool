package racloop



class ResetCabPriceJob {
	def cabDetailsService
	static triggers = {
		cron name: 'resetCabPrice', cronExpression: "0 0 2 * * ?"
	}

    def execute() {
		log.info "Starting to clear Cab Price template Map"
        cabDetailsService.resetCabPriceMap()
		log.info "Sucessfully cleared Cab Price template Map"
    }
}
