package com.racloop.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.racloop.Constant;

@DynamoDBTable(tableName = "RacloopUser")
public class RacloopUser {
	
	private String mobile;
	private String email;
	private String fullName;
	private String emailHash;
	private String overallRating;
	private Long version;

	public RacloopUser(){
		
	}

	@DynamoDBHashKey(attributeName="Mobile") 
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	@DynamoDBIndexHashKey(globalSecondaryIndexName = "Email-index", attributeName = "Email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@DynamoDBAttribute(attributeName="FullName") 
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmailHash() {
		return emailHash;
	}
	
	public String getOverallRating() {
		return overallRating;
	}

	public void setOverallRating(String overallRating) {
		this.overallRating = overallRating;
	}

	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
	}
	
	@DynamoDBIgnore
	public String getGravatarUri() { 
		return Constant.GRAVATAR_URL + emailHash + Constant.GRAVATAR_URL_SUFFIX;
	}

	@DynamoDBVersionAttribute
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
