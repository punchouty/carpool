package com.racloop.promotion

class PromotionMetaData {
	
	PromotionEvent promotionEvent
	
	Date startDate
	
	Date endDate

    static constraints = {
    }
	
	public boolean isPromotionValid(Date currentDate) {
		boolean isValid = false
		if(currentDate.compareTo(startDate)>=0 && currentDate.compareTo(endDate)<=0) {
			isValid = true
		}
		
		return isValid
	}
}
