package com.racloop

import grails.converters.JSON
import grails.util.Environment

class SampleDataController {
	
	def testDataService
	def smsService
	def grailsApplication
	def journeyDataService
	def userReviewService
	def cabDetailsService
	def autoMatcherService
	def recurrenceJourneyService
	def houseKeepingService

    def index() { 
		render "Empty Implementation"
	}
	
	/**
	 * delete all test journey data 
	 * http://localhost:8080/app/sampleData/deleteDataForUser?mobile=7307392447&secret=s3cr3t or http://www.racloop.com/deleteDataForUser?mobile=7307392447&secret=s3cr3t
	 * @return
	 */
	def deleteDataForUser(){
		log.info("Params : ${params}");
		def mobile = params.mobile
		def secret = params.secret
		def message = null;
		String deletePassword = grailsApplication.config.grails.delete.user.password
		if(secret.equals(deletePassword)) {
			def successMessage = testDataService.deleteDataForUser(secret, mobile);
			message = "Operation results : ${successMessage}"
		}
		else {
			message = "Wrong Password"
		}
		render message
	}
	
	/**
	 * delete all test journey data 
	 * http://localhost:8080/app/sampleData/deleteUser?mobile=7307392447&secret=s3cr3t or https://www.racloop.com/sampleData/deleteUser?mobile=7890123467&secret=s3cr3t
	 * @return
	 */
	def deleteUser(){
		log.info("Params : ${params}");
		def mobile = params.mobile
		def secret = params.secret
		def message = null;
		String deletePassword = grailsApplication.config.grails.delete.user.password
		if(secret.equals(deletePassword)) {
			def successMessage = testDataService.deleteDataForUser(secret, mobile);
			if(successMessage.equals("success")) {
				successMessage = testDataService.deleteUser(secret, mobile);
			}
			message = "Operation results : ${successMessage}"
		}
		else {
			message = "Wrong Password"
		}
		render message
	}
	
//	/**
//	 * delete all test journey data 
//	 * http://localhost:8080/app/sampleData/delete or http://www.racloop.com/app/sampleData/delete
//	 * @return
//	 */
//	def delete(){
//		testDataService.deleteDataForDev();
//		render "data deleted"
//	}
//	
	/**
	 * delete all test journey data as well as three test users 
	 * After that it recreate users as well as journey data
	 * http://localhost:8080/app/sampleData/deleteAll?secret=s3cr3t or http://www.racloop.com/sampleData/deleteAll?secret=s3cr3t
	 * @return
	 */
	def deleteAll(){
		log.info("Params : ${params}");
		def secret = params.secret
		def message = null;
		String deletePassword = grailsApplication.config.grails.delete.user.password
		if(secret.equals(deletePassword)) {
			testDataService.deleteAll();
			def successMessage = "Successfully deleted all data";
			message = "Operation results : ${successMessage}"
		}
		else {
			message = "Wrong Password"
		}
		render message
		
		render "all data deleted"
	}
	
	/**
	 * delete all test journey data as well as three test users 
	 * After that it recreate users as well as journey data
	 * number represent how many journey per users wil be created
	 * http://localhost:8080/app/sampleData/refresh?number=3&secret=s3cr3t or http://www.racloop.com/sampleData/refresh?number=3&secret=s3cr3t
	 * @return
	 */
	def refresh(){
		log.info("Params : ${params}");
		def secret = params.secret
		def message = null;
		int number = 1
		if(params.number != null) {
			number = Integer.parseInt(params.number);
		}
		if(number <= 1) number = 1;
		String deletePassword = grailsApplication.config.grails.delete.user.password
		if(secret.equals(deletePassword)) {
			testDataService.refreshAll(number);
			def successMessage = "Successfully refreshed all data";
			message = "Operation results : ${successMessage}"
		}
		else {
			message = "Wrong Password"
		}
		render message
	}
	
	/**
	 * count total number of journeys for today
	 * http://localhost:8080/app/sampleData/count or http://www.racloop.com/app/sampleData/count
	 * @return
	 */
	def count(){
		def journeys = journeyDataService.findAllJourneysForADate(new Date());
		render "number of journeys for today ${journeys?.size()}"
	}
	
	def sendSms() {
		log.info("Params : ${params}")
		def mobile = params.mobile
		def message = params.message
		def secret = params.secret
		boolean success = smsService.sendSms(mobile, message)
		if(success) {
			render "Success"
		}
		else {
			render "Fail"
		}
	}
	
	def runArchiveJob() {
		if (Environment.current == Environment.DEVELOPMENT) {
			userReviewService.markUsersForPendingReview();
			render "Operation successful"
		}
		else {
			render "Operation notsupported"
		}
	}
	
	def testEmail() {
		throw new Exception("Testing Email")
	}
	
	def resetCabPriceMap(){
		cabDetailsService.resetCabPriceMap()
		render "Cab Price Map cleared"
	}
	
	/**
	 * get verification code 
	 * http://localhost:8080/app/sampleData/verificationCode?mobile=7307392447&secret=s3cr3t
	 * @return
	 */
	def verificationCode(){
		log.info("Params : ${params}");
		def mobile = params.mobile
		def secret = params.secret
		String verificationCode = testDataService.getVerificationCode(secret, mobile);
		render verificationCode
	}
	
	def testAuto(){
		def result = autoMatcherService.sendAutoMatchNotificaiton()
		render "Success"
	}
	
	def testRecurrence() {
		recurrenceJourneyService.activateJourneys()
		render "Activated suceesully"
	}
	
	def restoreES() {
		houseKeepingService.restoreESData()
		render "All data loaded."
	}
	
	def doLogsHousekeeping(){
		houseKeepingService.archiveAnalyticsLogs()
		render "Housekeeping done. Data uploaded to S3"
	}
	
	def generateJourneyReport() {
		def json = journeyDataService.getJourneyReport(new Date()) as JSON 
		json.prettyPrint = true
		render json 
	}
}
