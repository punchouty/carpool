package com.racloop.domain;

import java.util.HashSet;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "AutoMatchJourney")
public class JourneyAutoMatch {
	
	private String primaryJourneyId;
	private Set<String>autoMatchedJourneyIds;
	
	
	 
	@DynamoDBHashKey(attributeName="PrimaryJourneyId")
	public String getPrimaryJourneyId() {
		return primaryJourneyId;
	}
	public void setPrimaryJourneyId(String primaryJourneyId) {
		this.primaryJourneyId = primaryJourneyId;
	}
	
	@DynamoDBAttribute(attributeName="AutoMatchedJourneyIds")
	public Set<String> getAutoMatchedJourneyIds() {
		return autoMatchedJourneyIds;
	}
	public void setAutoMatchedJourneyIds(Set<String> autoMatchedJourneyIds) {
		this.autoMatchedJourneyIds = autoMatchedJourneyIds;
	}
	
	public void addAutoMatchedJourneyIds(Set<String> newMatchedJourneyIds) {
		Set<String> autoMatchedJourneyIds = this.getAutoMatchedJourneyIds();
		if(autoMatchedJourneyIds == null || autoMatchedJourneyIds.isEmpty()){
			autoMatchedJourneyIds = new HashSet<String>();
		}
		autoMatchedJourneyIds.addAll(newMatchedJourneyIds);
		
		this.setAutoMatchedJourneyIds(autoMatchedJourneyIds);
		return;
	}

}
