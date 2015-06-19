package racloop

class CleanDummyJourneyJob {
	
	def workflowDataService
	def concurrent = false

	static triggers = {
		simple name: 'cleanDummyJourneyJobTrigger', startDelay: 10*60*1000, repeatInterval: 120*60*1000, repeatCount: -1
	}
	def group = "CleanDummyJourneyGroup"
	def description = "Cancels the requested journeys (assumes the dummy the user point of view)"

	def execute() {
		workflowDataService.cancelAllAgedDummyJourneyRequest()
	}
}
