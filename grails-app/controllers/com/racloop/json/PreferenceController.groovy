package com.racloop.json

import com.racloop.mobile.data.response.MobileResponse
import grails.converters.JSON

class PreferenceController {

    def makeRecurring() {def json = request.JSON
		log.info("makeRecurring json ${json}")
		MobileResponse mobileResponse = new MobileResponse();
		mobileResponse.success = true;
		render mobileResponse as JSON;
	}
}
