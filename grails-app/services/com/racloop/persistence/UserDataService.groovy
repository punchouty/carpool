package com.racloop.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.racloop.domain.RacloopUser;

import grails.transaction.Transactional


@Transactional
class UserDataService {

	def amazonWebService
	
	def findUserByMobile(String mobile) {
		RacloopUser user = amazonWebService.dynamoDBMapper.load(RacloopUser.class, mobile, new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));
		return user;
	}
	
    def saveUser(RacloopUser user) {
		amazonWebService.dynamoDBMapper.save(user, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    }
}
