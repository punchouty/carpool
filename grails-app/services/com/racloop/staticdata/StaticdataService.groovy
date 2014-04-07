package com.racloop.staticdata

class StaticdataService {

    public String getSaticValueBasedOnKey(String staticDataKey) {
		StaticData data  = StaticData.find {staticDataKey == staticDataKey}
		return data?.pageData
		
	}
}
