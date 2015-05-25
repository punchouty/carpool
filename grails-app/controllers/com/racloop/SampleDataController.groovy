package com.racloop

import java.text.SimpleDateFormat;

import grails.util.Environment
import liquibase.util.csv.opencsv.CSVReader

class SampleDataController {
	
	def sampleDataService
	def smsService
	def grailsApplication
	def journeyDataService;
	def roleService
	def groupService

    def index() { 
		render "Empty Implementation"
	}
	
//	def delete() {		
//		if(true) render "Delete is disable like this"
//		Boolean deleteEnabled = grailsApplication.config.grails.enable.delete.all
//		if(deleteEnabled) {
//			def secret = params.secret
//			if(secret.equals("cleanitup")) {
//				sampleDataService.deleteSampleData();
//				Journey.executeUpdate('delete from Journey');
//				User.findAll ().each {
//					it.activeJourneys.clear();
//					it.save();
//				}
//				render "Delete Complete"
//			}
//			else {
//				render "Wrong Password"
//			}
//		}
//		else {
//			render "Delete is disable like this"
//		}		
//	}
	
	/**
	 * http://localhost:8080/app/sampleData/delete?secret=051525&mobile=7307392447&deleteUser=false
	 * @return
	 */
	def delete() {
		SimpleDateFormat sdf = new SimpleDateFormat("mmyydd");
		String date = sdf.format(new Date());
		def secret = params.secret
		if(secret != date) {
			String mobile = params.mobile
			Profile profile = Profile.findByMobile(mobile);
			if(profile != null) {
				User user = profile.owner;
				String message = journeyDataService.deleteAllDataForUser(mobile);
//				Boolean deleteUser =params.deleteUser
//				if(deleteUser == true) {
//					def roles = user.roles;
//					roles.each { role ->
//						log.info("Removing role relationship : " + role.name)
//						roleService.deleteMember(user, role);
//					}
//					def groups = user.groups
//					groups.each { group ->
//						log.info("Removing group relationship : " + group.name)
//						groupService.deleteMember(user, group);
//					}
//					user.save(flush: true)
//					profile.delete();
//					user.delete();
//					message = message + "User and Profile also deleted"
//				}
				render message;
			}
			else {
				render "Invalid user mobile"
			}
		}
		else {
			render "Wrong Password"
		}
	}
	
	def populate() {	
		Environment.executeForCurrentEnvironment {
			development {
				sampleDataService.populateSampleData();
				render "Population Complete"
			}
			test {
				render "Empty Implementation"
			}
			production {
				render "Empty Implementation"
			}
		}	
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
}
