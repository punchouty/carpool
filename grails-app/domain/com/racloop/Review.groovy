package com.racloop

class Review {
	
	User reviewer
	User reviewee
	String comment
	String journeyId
	boolean isReviewerADriver = false
	
	private transient final double  w_safety = 0d
	private transient final double  w_comfort = 0d
	private transient final double  w_punctualty = 1d
	private transient final double  w_overall = 1d
	
	//for driver only
	//double luggage
	//double loud
	
	//for riders
	Double safety =0D// 1-5 scale
	Double comfort = 0D// 1-5 scale
	
	// for both driver and rider
	Double punctualty // 1-5 scale
	Double overall // 1-5 scale

    static constraints = {
    }
	
	static mapping = {
		comment type: 'text'
	}
	
	static transients = ["w_safety","w_safety","w_punctualty", "w_overall"]
	
	
	public double getAverageRating() {

		double weightedSum = (punctualty * w_punctualty) + (overall * w_overall)
		double totalWeight =  w_punctualty + w_overall
		if(!isReviewerADriver) {
			weightedSum = weightedSum + (safety * w_safety) + (comfort * w_comfort)
			totalWeight = totalWeight + w_safety + w_comfort
		}
		double reviewRating = weightedSum/totalWeight
		return reviewRating
	}
}
