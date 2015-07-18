package com.racloop.staticdata

class CabPrice {

	String city
	String cabPriceCodeAsString
	
	
	static mapping = {
		cache true
		cabPriceCodeAsString type: 'text'
	}
	
	static constraints = {
		
    }
}
