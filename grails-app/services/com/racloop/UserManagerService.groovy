package com.racloop

import grails.transaction.Transactional

import org.apache.shiro.crypto.hash.Sha256Hash

import com.racloop.notification.promotion.model.PromotionMessage;
import com.racloop.promotion.PromotionEvent;

@Transactional
class UserManagerService {
	
	def jmsService
	def userService

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
		profile.owner.enabled = false
		profile.save();
		profile.owner.save(flush: true);
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
			profile.owner.enabled = false
			profile.save()
			profile.owner.save(flush:true)
			
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
	
	def String generateUserCode(String fullName) {
		if(fullName){
			StringBuffer userCode  = new StringBuffer()
			String[] nameTokens = fullName.split(" ")
			int numberOftoken = nameTokens.length
			String firstName = nameTokens[0].trim().toUpperCase()
			userCode.append(firstName.substring(0, Math.min(firstName.length(), 3)).padRight(3,'X'))
			if(numberOftoken>=2) {
				String lastName = nameTokens[1].trim().toUpperCase()
				if(lastName){
					userCode.append(lastName.substring(0, Math.min(firstName.length(), 1)))
				} 
				else {
					userCode.append("Z")
				}
			}
			else {
				userCode.append("Z")
			}
			
			userCode.append(generateThreeDigitCode())
			return userCode.toString() 
		}
		else {
			return null
		}
	}
	
	private int generateThreeDigitCode() {
		Random random = new Random();
		int n = 100 + random.nextInt(900);
		return n;
	}
	
	private boolean validateAndApplyReferalCode(String userCode) {
		User user = User.findByUserCode(userCode)
		if(user) {
			PromotionMessage promotionMessage = new PromotionMessage()
			promotionMessage.promotionEvent = PromotionEvent.USER_REFERAL_EVENT
			promotionMessage.messageData = [benificaryUserName : user.username]
			jmsService.send(queue: Constant.NOTIFICATION_PROMOTION_MESSAGE_QUEUE, promotionMessage);
			return true
		}
		else {
			return false
		}
	}
}
