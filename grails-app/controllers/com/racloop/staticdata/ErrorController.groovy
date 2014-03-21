package com.racloop.staticdata

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
	}

	def notFound() {
		
	}

	def forbidden() {
	}
}
