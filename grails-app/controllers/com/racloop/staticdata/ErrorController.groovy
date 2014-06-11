package com.racloop.staticdata

import grails.converters.JSON

class ErrorController {

	def grailsApplication
	def mailService

	def internalError() {
		String fromEmail = grailsApplication.config.grails.mail.username;
		Exception exception = request.exception
		try {
			mailService.sendMail {
				multipart true
				to grailsApplication.config.grails.email.exception.one,grailsApplication.config.grails.email.exception.two
				subject "500 Error"
				from fromEmail
				//body (view: "error")
				html g.render(template: "/templates/email/error_email", model: [exception: exception]).toString()
			}
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
			json  {render errorMap as JSON}
		}
	}

	def forbidden() {
		def errorMap = [ error: "true", code: "403" ]
		withFormat {
			json  {render errorMap as JSON}
		}
	}
}
