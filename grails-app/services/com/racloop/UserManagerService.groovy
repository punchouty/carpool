package com.racloop

import java.util.Random;

import com.racloop.Profile;

import grails.transaction.Transactional

@Transactional
class UserManagerService {
	
	def jmsService

	/**
	 * Verification during signup
	 * @param profile
	 * @return
	 */
    def setUpMobileVerificationDuringSignUp(Profile profile) {
		profile.verificationCode = generateVerificationCode();
		profile.save();
		def  messageMap =[mobile:profile.mobile, verificationCode:profile.verificationCode]
		jmsService.send(queue: Constant.MOBILE_VERIFICATION_QUEUE, messageMap);
    }
	
	/**
	 * Verification during edit profile on mobile
	 * 
	 * @param oldMobile
	 * @param newMobile
	 * @return
	 */
	def setUpVerificationForMobileChange(String mobile) {
		Profile profile = Profile.findByMobile(mobile);
		profile.verificationCode = generateVerificationCode();
		profile.isVerified = false
		profile.save();
		def  messageMap =[(Constant.MOBILE_KEY) : mobile, (Constant.VERIFICATION_CODE_KEY):profile.verificationCode]
		jmsService.send(queue: Constant.MOBILE_VERIFICATION_QUEUE, messageMap);
	}
	
	/**
	 * Resend Sms
	 * @param mobile
	 * @return
	 */
	def setUpMobileVerification(String mobile) {
		Profile profile = Profile.findByMobile(mobile);
		if(profile) {
			profile.verificationCode = generateVerificationCode();
			profile.isVerified = false
			profile.save();
			def  messageMap =[(Constant.MOBILE_KEY) : mobile, (Constant.VERIFICATION_CODE_KEY):profile.verificationCode]
			jmsService.send(queue: Constant.MOBILE_VERIFICATION_QUEUE, messageMap);
			return GenericStatus.SUCCESS;
		}
		else {
			return GenericStatus.INVALID_STATE
		}
	}
	
	/**
	 * Verification of mobile
	 * 
	 * @param mobile
	 * @param verificationCode
	 * @return
	 */
	def verify(String mobile, String verificationCode) {
		Profile profile = Profile.findByMobile(mobile);
		if(profile) {
			if(profile.verificationCode == verificationCode) {
				profile.isVerified = true
				profile.save()
				profile.owner.enabled = true
				profile.owner.save();
				return GenericStatus.SUCCESS
			}
			else {
				GenericStatus.FAILURE
			}
		}
		else {
			return GenericStatus.INVALID_STATE
		}
	}
	
	private int generateVerificationCode() {
		Random rnd = new Random();
		int n = 100000 + rnd.nextInt(900000);
		return n;
	}
}
