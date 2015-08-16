package racloop



class ResetCabPriceJob {
	def cabDetailsService
	def staticdataService
	static triggers = {
		cron name: 'resetCabPrice', cronExpression: "0 0 2 * * ?"
	}

		def execute() {
		if (staticdataService.canRunJob()) {
			log.info "Starting to clear Cab Price template Map"
	        cabDetailsService.resetCabPriceMap()
			log.info "Sucessfully cleared Cab Price template Map"
		}
		else {
			log.info "Not running ResetCabPriceJob job as I am not configured to run on the machine"
		}
		
	}
}
