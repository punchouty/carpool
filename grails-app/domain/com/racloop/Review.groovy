package com.racloop

class Review {
	
	User reviewer
	User reviewee
	String comment
	boolean isReviewerADriver
	
	//for driver only
	//double luggage
	//double loud
	
	//for riders
	double safety // 1-5 scale
	double comfort // 1-5 scale
	
	// for both driver and rider
	double punctualty // 1-5 scale
	double overall // 1-5 scale

    static constraints = {
    }
	
	static mapping = {
		comment type: 'text'
	}
}
