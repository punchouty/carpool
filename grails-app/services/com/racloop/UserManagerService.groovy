package com.racloop

import grails.transaction.Transactional

import org.apache.shiro.crypto.hash.Sha256Hash

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
	def setUpVerificationForMobileChange(String mobile, String email=null) {
		Profile profile = getUserProfile(mobile, email)
		profile.verificationCode = generateVerificationCode();
		profile.isVerified = false
		profile.save(flush: true);
		def  messageMap =[(Constant.MOBILE_KEY) : mobile, (Constant.VERIFICATION_CODE_KEY):profile.verificationCode]
		jmsService.send(queue: Constant.MOBILE_VERIFICATION_QUEUE, messageMap);
	}
	
	/**
	 * Resend Sms
	 * @param mobile
	 * @return
	 */
	def setUpMobileVerification(String mobile, String email=null) {
		Profile profile = getUserProfile(mobile,email)
		if(profile) {
			profile.verificationCode = generateVerificationCode();
			profile.isVerified = false
			profile.save(flush:true);
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
	def verify(String mobile, String verificationCode, String email=null) {
		Profile profile = getUserProfile(mobile, email)
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
	
	private Profile getUserProfile(String mobile, String email){
		Profile profile = null
		if(email){
			profile = Profile.findByMobileAndEmail(mobile, email)
		}
		else {
			profile = Profile.findByMobile(mobile)
		}
		
		return profile
	}
	
	
	def updateUserDetailsIfRequired(User user, String facebookId) {
		boolean updateUser = false
		def pwEnc = new Sha256Hash(Constant.DEFAULT_PASSWORD)
		def crypt = pwEnc.toHex()
		if(!user.facebookId) {
			user.facebookId = facebookId
			updateUser = true
		}
		if(!crypt.equals(user.passwordHash)) {
			user.passwordHash = crypt
			updateUser = true
		}
		if(updateUser) {
			userService.updateUser(user)
		}
		user.pass= Constant.DEFAULT_PASSWORD
	}
}
