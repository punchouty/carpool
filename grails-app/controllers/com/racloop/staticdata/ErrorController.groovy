package com.racloop.staticdata

import grails.converters.JSON

class ErrorController {

	def grailsApplication
	def emailService

	def internalError() {
		String fromEmail = grailsApplication.config.grails.mail.username;
		Exception exception = request.exception
		try {
			List toList = [grailsApplication.config.grails.email.exception.one,grailsApplication.config.grails.email.exception.two]
			
			emailService.sendMailToMultipleRecipients(toList, "Exception from ${java.net.InetAddress.getLocalHost().getHostName()}", g.render(template: "/templates/email/error_email", model: [exception: exception]).toString())
			
		}
		catch (Exception e) {
			log.info e
		}
		def errorMap = [ error: "true", code: "500", message: exception.message ]
		withFormat {
			html exception: exception
			json  {render errorMap as JSON}
		}
	}

	def notFound() {
		def errorMap = [ error: "true", code: "404" ]
		withFormat {
			html code: "404"
			json  {render errorMap as JSON}
		}
	}

	def forbidden() {
		def errorMap = [ error: "true", code: "403" ]
		withFormat {
			html code: "403"
			json  {render errorMap as JSON}
		}
	}
}
