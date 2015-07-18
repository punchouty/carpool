package com.racloop.integration

import java.util.Map;

class CabDetailsService {
	def grailsApplication
	
	private Map cityViseCabPriceTemplateMap=[:]
	
	private final String DELHI = "DELHI"
	
	def Map getDelhiCabPrices(Double distanceInKm, Integer timeInSeconds){
		Binding binding = new Binding() ;
		binding.setVariable( "distanceInKm", distanceInKm) ;
		binding.setVariable( "timeInSeconds", timeInSeconds) ;
		GroovyShell shell=new GroovyShell(binding)
		String delhiPriceTemplate = cityViseCabPriceTemplateMap.get(DELHI)
		if(!delhiPriceTemplate){
			log.warn ("Unable to locat the cab price temaplate in memory. Reading from file.")
			this.populateCityViseCabPriceTemplate()
			delhiPriceTemplate = cityViseCabPriceTemplateMap.get(DELHI)
		}
		def priceMap = shell.evaluate(delhiPriceTemplate)
		return priceMap
	}
	
	def populateCityViseCabPriceTemplate() {
		File delhiPriceTemplateFile = grailsApplication.mainContext.getResource("resources/DelhiCabPrice.groovy").file
		String delhiPriceTemplate = delhiPriceTemplateFile.text
		cityViseCabPriceTemplateMap.put(DELHI, delhiPriceTemplate)
	}
	

}
