package com.racloop.persistence

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.racloop.domain.LoginDetail;
import com.racloop.domain.RacloopUser;

import grails.plugin.nimble.core.UserBase;
import grails.transaction.Transactional


@Transactional
class UserDataService {

	def awsService
	
	def findUserByMobile(String mobile) {
		RacloopUser user = awsService.dynamoDBMapper.load(RacloopUser.class, mobile, new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));
		return user;
	}
	
    def saveUser(RacloopUser user) {
		awsService.dynamoDBMapper.save(user, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
    }
	
	def deleteUserByMobile(String mobile) {
		RacloopUser user = awsService.dynamoDBMapper.load(RacloopUser.class, mobile, new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));
		if(user) awsService.dynamoDBMapper.delete(user);
		return user;
	}
	
	void createLoginRecord(HttpServletRequest request) {
		LoginDetail record = new LoginDetail();
		String resolvedIp = getClientIpAddress(request)
		
		record.remoteAddr = resolvedIp
		record.remoteHost = request.getRemoteHost()
		record.userAgent = request.getHeader("User-Agent")
		
		def user = UserBase.get(SecurityUtils.getSubject()?.getPrincipal())
		
		record.userMobile = user.username
		awsService.dynamoDBMapper.save(record, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE));
	}
	
	private String getClientIpAddress(request) {
		String ip = request.getHeader("X-Forwarded-For")
		if(isNullOrUnknown(ip)) {
			ip = request.getHeader("Proxy-Client-IP")
		}
		if(isNullOrUnknown(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP")
		}
		if(isNullOrUnknown(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR")
		}
		if(isNullOrUnknown(ip)) {
			ip = request.getRemoteAddr()
		}

		if(ip.contains(",")) {
			ip = ip.split(",")[0]
		}

		return ip
	}

	private boolean isNullOrUnknown(String str) {
		(null == str || str.trim().length() == 0 || "unknown".equalsIgnoreCase(str))
	}
}
