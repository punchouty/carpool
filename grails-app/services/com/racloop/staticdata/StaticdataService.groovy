package com.racloop.staticdata

class StaticdataService {

    public String getSaticValueBasedOnKey(String key) {
		StaticData data  = StaticData.find {key == key}
		return data?.data
		
	}
}
