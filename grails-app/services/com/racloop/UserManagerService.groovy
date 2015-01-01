package com.racloop

import java.util.Random;

import com.racloop.Profile;

import grails.transaction.Transactional

@Transactional
class UserManagerService {
	
	def jmsService

    def setUpMobileVerificationDuringSignUp(Profile profile) {
		profile.verificationCode = generateVerificationCode();
		profile.save();
		def  messageMap =[mobile:profile.mobile, verificationCode:profile.verificationCode]
		jmsService.send(queue: Constant.MOBILE_VERIFICATION_QUEUE, messageMap);
    }
	
	def setUpMobileVerificationExistingUser(String mobile) {
		Profile profile = Profile.findByMobile(mobile);
		if(profile) {
			if(profile.isVerified) {
				profile.verificationCode = generateVerificationCode();
				profile.isVerified = false
				profile.save();
				def  messageMap =[(Constant.MOBILE_KEY) : mobile, (Constant.VERIFICATION_CODE_KEY):profile.verificationCode]
				jmsService.send(queue: Constant.MOBILE_VERIFICATION_QUEUE, messageMap);
				return true;
			}
		}
		return false
	}
	
	def verify(String mobile, String verificationCode) {
		Profile profile = Profile.findByMobile(mobile);
		if(profile) {
			if(profile.verificationCode == verificationCode) {
				profile.isVerified = true
				profile.save()
				profile.owner.enabled = true
				profile.owner.save();
				return true
			}
		}
		return false
	}
	
	private int generateVerificationCode() {
		Random rnd = new Random();
		int n = 100000 + rnd.nextInt(900000);
		return n;
	}
}
