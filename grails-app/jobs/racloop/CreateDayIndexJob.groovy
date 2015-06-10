package racloop

@Deprecated
class CreateDayIndexJob {
	
	def elasticSearchService
	
    static triggers = {
      //cron name:'cronTrigger', startDelay:10000, cronExpression: '0 0 3 * * ?' //fire daily at 3:00 a.m.
	  cron name:'cronTrigger', startDelay:10000, cronExpression: '0 0 3 ? * SUN' //fire every sunday early morning 3 am
    }

    def execute() {
//		addIndexes();
//		closeIndexes();
    }
	
	private void addIndexes() {
		elasticSearchService.createMainIndex();
	}
	
	private void closeIndexes() {
		elasticSearchService.closeMainIndexForPreviousWeek();
	}
}
