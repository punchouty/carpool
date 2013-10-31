package racloop

class CreateDayIndexJob {
	
	def elasticSearchService
	
    static triggers = {
      cron name:'cronTrigger', startDelay:10000, cronExpression: '0 0 3 * * ?'
    }

    def execute() {
		addIndexes();
		closeIndexes();
    }
	
	private void addIndexes() {
		Date date = new Date();
		for (int i = 0; i < 10; i++) {
			elasticSearchService.createIndexIfNotExistsForDate(date);
			date = date.next()			
		}
	}
	
	private void closeIndexes() {
		Date date = new Date().previous();
		elasticSearchService.closeIndexIfExistsForDate(date);
	}
}
