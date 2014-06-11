package com.racloop.staticdata

class StaticData {

    String staticDataKey
	String pageData
	
	static mapping = {
		cache true
		pageData type: 'text'
	}
}
