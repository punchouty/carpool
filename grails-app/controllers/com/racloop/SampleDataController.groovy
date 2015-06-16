package com.racloop

import java.text.SimpleDateFormat;

import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

class SampleDataController {
	
	def testDataService
	def smsService
	def grailsApplication
	def journeyDataService
	def userReviewService

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
		def successMessage = testDataService.deleteDataForUser(secret, mobile);
		render "Operation results : ${successMessage}"
	}
	
	/**
	 * delete all test journey data 
	 * http://localhost:8080/app/sampleData/deleteUser?mobile=7307392447&secret=s3cr3t or http://www.racloop.com/deleteDataForUser?mobile=7307392447&secret=s3cr3t
	 * @return
	 */
	def deleteUser(){
		log.info("Params : ${params}");
		def mobile = params.mobile
		def secret = params.secret
		def successMessage = testDataService.deleteUser(secret, mobile);
		render "Operation results : ${successMessage}"
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
	 * http://localhost:8080/app/sampleData/deleteAll or http://www.racloop.com/sampleData/deleteAll
	 * @return
	 */
	def deleteAll(){
		testDataService.deleteAll();
		render "all data deleted"
	}
	
	/**
	 * delete all test journey data as well as three test users 
	 * After that it recreate users as well as journey data
	 * number represent how many journey per users wil be created
	 * http://localhost:8080/app/sampleData/refresh?number=3 or http://www.racloop.com/sampleData/refresh?number=3
	 * @return
	 */
	def refresh(){
		log.info("Params : ${params}")
		int number = 1
		if(params.number != null) {
			number = Integer.parseInt(params.number);
		}
		if(number <= 1) number = 1;
		testDataService.refreshAll(number);
		render "data refreshed"
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
		def mobile = params.mobile
		def message = params.message
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
}
