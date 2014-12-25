package com.racloop.staticdata

class StaticData {

    String staticDataKey
	String pageData
	
	static auditable = true
	
	static mapping = {
		cache true
		pageData type: 'text'
	}
}
