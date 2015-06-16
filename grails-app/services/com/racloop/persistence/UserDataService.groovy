package com.racloop.persistence

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.racloop.domain.RacloopUser;

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
}
