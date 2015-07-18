package com.racloop.integration

import com.racloop.staticdata.CabPrice;


class CabDetailsService {
	
	def grailsApplication
	def awsService
	
	private Map cityViseCabPriceTemplateMap=[:]
	
	public static final String DELHI = "DELHI"
	
	def Map getDelhiCabPrices(Double distanceInKm, Integer timeInSeconds){
		Binding binding = new Binding() ;
		binding.setVariable( "distanceInKm", distanceInKm) ;
		binding.setVariable( "timeInSeconds", timeInSeconds) ;
		GroovyShell shell=new GroovyShell(binding)
		String delhiPriceTemplate = cityViseCabPriceTemplateMap.get(DELHI)
		if(!delhiPriceTemplate){
			log.warn ("Unable to locat the cab price temaplate in memory. Reading from DB.")
			delhiPriceTemplate = this.findCabPriceTemplateForACity(DELHI)
			cityViseCabPriceTemplateMap.put(DELHI, delhiPriceTemplate)
		}
		def priceMap = shell.evaluate(delhiPriceTemplate)
		return priceMap
	}
	
	def populateCityViseCabPriceTemplateFromFile() {
		File delhiPriceTemplateFile = grailsApplication.mainContext.getResource("resources/DelhiCabPrice.groovy").file
		String delhiPriceTemplate = delhiPriceTemplateFile.text
		cityViseCabPriceTemplateMap.put(DELHI, delhiPriceTemplate)
		this.saveCabPriceTemplate(DELHI, delhiPriceTemplate)
	}
	
	def String findCabPriceTemplateForACity(String city) {
		CabPrice cabPrice = CabPrice.findByCity(city);
		return cabPrice?.cabPriceCodeAsString
	}
	
	def saveCabPriceTemplate(String city, String cabPriceCode) {
		CabPrice cabPrice = CabPrice.findByCity(city);
		if(cabPrice) {
			cabPrice.cabPriceCodeAsString = cabPriceCode
		}
		else {
			cabPrice = new CabPrice()
			cabPrice.city = city
			cabPrice.cabPriceCodeAsString = cabPriceCode
		}
		cabPrice.save()
		
	}
	
	def resetCabPriceMap(){
		this.cityViseCabPriceTemplateMap = [:]
	}

}
