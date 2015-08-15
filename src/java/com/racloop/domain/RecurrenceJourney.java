package com.racloop.domain;

import java.util.HashSet;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "RecurrenceJourney")
public class RecurrenceJourney {
	
	private String id;
	private Set<String> parentJourenyIds;

	@DynamoDBHashKey(attributeName = "Id")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@DynamoDBAttribute(attributeName="ParentJourenyIds") 
	public Set<String> getParentJourenyIds() {
		return parentJourenyIds;
	}
	public void setParentJourenyIds(Set<String> parentJourenyIds) {
		this.parentJourenyIds = parentJourenyIds;
	}
	
	@Override
	public String toString() {
		return "RecurrenceJourney [id=" + id + ", parentJourenyIds="
				+ parentJourenyIds + "]";
	}
	
	public void addParentJourney(String journeyId){
		if(this.parentJourenyIds == null || this.parentJourenyIds.isEmpty()) {
			this.parentJourenyIds = new HashSet<String>();
		}
		
		this.parentJourenyIds.add(journeyId);
	}
	
	public void removeParentJourney(String journeyId) {
		if(parentJourenyIds != null) {
			parentJourenyIds.remove(journeyId);
		}
	}
	
	
}
