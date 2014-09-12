package com.racloop.marshaller

class CustomObjectMarshallers {

	List marshallers = []
	
	def register() {
		marshallers.each{ it.register() }
	}
}
