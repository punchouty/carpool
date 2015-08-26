package com.racloop.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.racloop.JourneyRequestCommand;
import com.racloop.journey.workkflow.WorkflowStatus;

@DynamoDBTable(tableName = "Journey")
public class Journey {

	private String id;
	private String mobile;
	private Date dateKey;
	private Date dateOfJourney;
	private String email;
	private String name;
	private Double fromLatitude;
	private Double fromLongitude;
	private String from;
	private Double toLatitude;
	private Double toLongitude;
	private String to;
	private Boolean isMale;
	private Boolean isDriver;
	private Boolean isTaxi;
	private Double tripDistance;
	private String tripUnit;
	private String ip;
	private Date createdDate = new Date();
	private String photoUrl;
	private Set<String> journeyPairIds;
	private Integer numberOfCopassengers = 0;
	private Boolean isSearchable;
	private Boolean isDummy = false;
	private Long version;
	private String statusAsParent = WorkflowStatus.NONE.getStatus();
	private String myStatus;
	private String myDirection;
	private String myPairId;
	private String[] myActions; 
	private Set<Journey> relatedJourneys = new HashSet<Journey>();
	private Set<JourneyPair> journeyPairs = new HashSet<JourneyPair>();
	private Integer tripTimeInSeconds;
	private String parentRecurringJourneyId;
	private Set<String> journeyRecurrence;
	private Boolean isRecurring;
	private Boolean hasAcceptedRequest;
	private Boolean femaleOnlySearch = false;
	private Set<String> incomingJourneyPairIds;
	private Set<String> outgoingJourneyPairIds;
	
	public JourneyRequestCommand convert() {
		JourneyRequestCommand journeyCommand = new JourneyRequestCommand();
		journeyCommand.setId(id);
		journeyCommand.setUser(getUser());
		journeyCommand.setEmail(email);
		journeyCommand.setMobile(mobile);
		journeyCommand.setName(name);
		journeyCommand.setIsMale(isMale);
		journeyCommand.setDateOfJourney(dateOfJourney);
		journeyCommand.setFrom(from);
		journeyCommand.setFromLatitude(fromLatitude);
		journeyCommand.setFromLongitude(fromLongitude);
		journeyCommand.setTo(to);
		journeyCommand.setToLatitude(toLatitude);
		journeyCommand.setToLongitude(toLongitude);
		journeyCommand.setIsDriver(isDriver);
		journeyCommand.setIsTaxi(isTaxi);
		journeyCommand.setTripDistance(tripDistance);
		journeyCommand.setIp(ip);
		journeyCommand.setCreatedDate(createdDate);
		journeyCommand.setPhotoUrl(photoUrl);
		journeyCommand.setTripTimeInSeconds(tripTimeInSeconds);
		journeyCommand.setFemaleOnlySearch(femaleOnlySearch);
		return journeyCommand;
	}
	
	public static Journey convert(JourneyRequestCommand requestCommand) {
		Journey journey = new Journey();
		journey.setId(requestCommand.getId());
		journey.setMobile(requestCommand.getMobile());
		journey.setDateOfJourney(requestCommand.getDateOfJourney());
		journey.setEmail(requestCommand.getUser());
		journey.setName(requestCommand.getName());
		journey.setFrom(requestCommand.getFrom());
		journey.setFromLatitude(requestCommand.getFromLatitude());
		journey.setFromLongitude(requestCommand.getFromLongitude());
		journey.setTo(requestCommand.getTo());
		journey.setToLatitude(requestCommand.getToLatitude());
		journey.setToLongitude(requestCommand.getToLongitude());
		journey.setIsMale(requestCommand.getIsMale());
		journey.setIsDriver(requestCommand.getIsDriver());
		journey.setIsTaxi(requestCommand.getIsTaxi());
		journey.setTripDistance(requestCommand.getTripDistance());
		journey.setIp(requestCommand.getIp());
		journey.setCreatedDate(requestCommand.getCreatedDate());
		journey.setPhotoUrl(requestCommand.getPhotoUrl());
		journey.setTripTimeInSeconds(requestCommand.getTripTimeInSeconds());
		journey.setFemaleOnlySearch(requestCommand.getFemaleOnlySearch());
		return journey;
	}
	
	public void addPairIdToJourney (String ... pairIds){
		Set<String> existingPairId = this.getJourneyPairIds();
		if(existingPairId == null || existingPairId.isEmpty()){
			existingPairId = new HashSet<String>();
		}
		for (String id: pairIds) {           
		    existingPairId.add(id);
		}
		
		this.setJourneyPairIds(existingPairId);
		return;
	}
	
	public void incrementNumberOfCopassengers(){
		if(this.numberOfCopassengers == null){
			this.numberOfCopassengers = 0;
		}
		assert this.numberOfCopassengers < 2;
		this.numberOfCopassengers++;
	}
	
	public void decrementNumberOfCopassengers(){
		if(this.numberOfCopassengers == null){
			this.numberOfCopassengers = 0;
		}
		assert this.numberOfCopassengers > 0;
		this.numberOfCopassengers--;
	}

	public void incrementNumberOfCopassengers(int count) {
		for (int i = 0; i < count; i++) {
			incrementNumberOfCopassengers();
		}
	}

	@Override 
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("id : " + id + ", ");
		buffer.append("mobile : " + mobile + ", ");
		buffer.append("dateOfJourney : " + dateOfJourney + ", ");
		buffer.append("email : " + email + ", ");
		buffer.append("name : " + name + ", ");
		buffer.append("from : " + from + ", ");
		buffer.append("to : " + to + ", ");
		buffer.append("journeyPairIds : " + journeyPairIds + ", ");
		buffer.append("isDummy : " + isDummy);
		buffer.append("isTaxi : " + isTaxi);
		return buffer.toString();
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Journey) {
			Journey other = (Journey)obj;
			return other.id.equals(this.id);
		}
		else {
			return false;
		}
	}
	
	@DynamoDBHashKey(attributeName = "Id")
	@DynamoDBAutoGeneratedKey
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@DynamoDBIndexHashKey(globalSecondaryIndexName = "Mobile-DateOfJourney-index", attributeName = "Mobile")
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = "Mobile-DateOfJourney-index", attributeName = "DateOfJourney")
	public Date getDateOfJourney() {
		return dateOfJourney;
	}
	public void setDateOfJourney(Date dateOfJourney) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			this.dateKey = dateFormat.parse(dateFormat.format(dateOfJourney));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.dateOfJourney = dateOfJourney;
	}

	@DynamoDBIndexHashKey(globalSecondaryIndexName = "DateKey-index", attributeName = "DateKey")
	public Date getDateKey() {
		return dateKey;
	}

	public void setDateKey(Date dateKey) {
		this.dateKey = dateKey;
	}
	
	@DynamoDBAttribute(attributeName="Email") 
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@DynamoDBAttribute(attributeName="FullName") 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@DynamoDBAttribute(attributeName="FromLatitude") 
	public Double getFromLatitude() {
		return fromLatitude;
	}
	public void setFromLatitude(Double fromLatitude) {
		this.fromLatitude = fromLatitude;
	}
	
	@DynamoDBAttribute(attributeName="FromLongitude") 
	public Double getFromLongitude() {
		return fromLongitude;
	}
	public void setFromLongitude(Double fromLongitude) {
		this.fromLongitude = fromLongitude;
	}
	
	@DynamoDBAttribute(attributeName="FromPlace") 
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
	@DynamoDBAttribute(attributeName="ToLatitude") 
	public Double getToLatitude() {
		return toLatitude;
	}
	public void setToLatitude(Double toLatitude) {
		this.toLatitude = toLatitude;
	}
	
	@DynamoDBAttribute(attributeName="ToLongitude") 
	public Double getToLongitude() {
		return toLongitude;
	}
	public void setToLongitude(Double toLongitude) {
		this.toLongitude = toLongitude;
	}
	
	@DynamoDBAttribute(attributeName="ToPlace") 
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
	@DynamoDBAttribute(attributeName="IsMale") 
	public Boolean getIsMale() {
		return isMale;
	}
	public void setIsMale(Boolean isMale) {
		this.isMale = isMale;
	}
	
	@DynamoDBAttribute(attributeName="IsDriver") 
	public Boolean getIsDriver() {
		return isDriver;
	}
	public void setIsDriver(Boolean isDriver) {
		this.isDriver = isDriver;
	}
	
	@DynamoDBAttribute(attributeName="IsTaxi") 
	public Boolean getIsTaxi() {
		return isTaxi;
	}

	public void setIsTaxi(Boolean isTaxi) {
		this.isTaxi = isTaxi;
	}
	
	@DynamoDBAttribute(attributeName="TripDistance") 
	public Double getTripDistance() {
		return tripDistance;
	}
	public void setTripDistance(Double tripDistance) {
		this.tripDistance = tripDistance;
	}
	
	@DynamoDBAttribute(attributeName="TripUnit") 
	public String getTripUnit() {
		return tripUnit;
	}
	public void setTripUnit(String tripUnit) {
		this.tripUnit = tripUnit;
	}
	
	@DynamoDBAttribute(attributeName="IpAddress") 
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@DynamoDBAttribute(attributeName="CreateDate") 
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@DynamoDBAttribute(attributeName="PhotoUrl") 
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	@DynamoDBAttribute(attributeName="JourneyPairIds")
	public Set<String> getJourneyPairIds() {
		return journeyPairIds;
	}

	public void setJourneyPairIds(Set<String> journeyPairIds) {
		this.journeyPairIds = journeyPairIds;
	}

	@DynamoDBAttribute(attributeName="NumberOfCopassengers")
	public Integer getNumberOfCopassengers() {
		return numberOfCopassengers;
	}

	public void setNumberOfCopassengers(Integer numberOfCopassengers) {
		this.numberOfCopassengers = numberOfCopassengers;
	}

	@DynamoDBAttribute(attributeName="IsSearchable")
	public Boolean getIsSearchable() {
		return isSearchable;
	}

	public void setIsSearchable(Boolean isSearchable) {
		this.isSearchable = isSearchable;
	}
	
	@DynamoDBAttribute(attributeName="IsDummy") 
	public Boolean getIsDummy() {
		return isDummy;
	}

	public void setIsDummy(Boolean isDummy) {
		this.isDummy = isDummy;
	}

	@DynamoDBVersionAttribute
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@DynamoDBAttribute(attributeName="StatusAsParent") 
	public String getStatusAsParent() {
		return statusAsParent;
	}

	public void setStatusAsParent(String statusAsParent) {
		this.statusAsParent = statusAsParent;
	}

	@DynamoDBIgnore
	public String getMyStatus() {
		return myStatus;
	}

	public void setMyStatus(String myStatus) {
		this.myStatus = myStatus;
	}

	@DynamoDBIgnore
	public String getMyPairId() {
		return myPairId;
	}

	public void setMyPairId(String myPairId) {
		this.myPairId = myPairId;
	}

	@DynamoDBIgnore
	public String[] getMyActions() {
		return myActions;
	}

	public void setMyActions(String[] myActions) {
		this.myActions = myActions;
	}

	@DynamoDBIgnore
	public String getMyDirection() {
		return myDirection;
	}

	public void setMyDirection(String myDirection) {
		this.myDirection = myDirection;
	}

	@DynamoDBIgnore
	public Set<Journey> getRelatedJourneys() {
		return relatedJourneys;
	}

	public void setRelatedJourneys(Set<Journey> relatedJourneys) {
		this.relatedJourneys = relatedJourneys;
	}

	@DynamoDBIgnore
	public Set<JourneyPair> getJourneyPairs() {
		return journeyPairs;
	}

	public void setJourneyPairs(Set<JourneyPair> journeyPairs) {
		this.journeyPairs = journeyPairs;
	}

	@DynamoDBIgnore
	public String getUser() {
		return email;
	}
	
	@DynamoDBAttribute(attributeName="TripTime") 
	public Integer getTripTimeInSeconds() {
		return tripTimeInSeconds;
	}
	public void setTripTimeInSeconds(Integer tripTimeInSeconds) {
		this.tripTimeInSeconds = tripTimeInSeconds;
	}
	
	public Journey resetMe() {
		this.id = null;
		this.journeyPairIds  = null;
		this.numberOfCopassengers = 0;
		this.journeyPairIds = null;
		this.version = null;
		return this;
	}

	@DynamoDBAttribute(attributeName="ParentRecurringJourneyId") 
	public String getParentRecurringJourneyId() {
		return parentRecurringJourneyId;
	}

	public void setParentRecurringJourneyId(String parentRecurringJourneyId) {
		this.parentRecurringJourneyId = parentRecurringJourneyId;
	}
	
	@DynamoDBAttribute(attributeName="JourneyRecurrence") 
	public Set<String> getJourneyRecurrence() {
		return journeyRecurrence;
	}

	public void setJourneyRecurrence(Set<String> journeyRecurrence) {
		this.journeyRecurrence = journeyRecurrence;
	}

	public Boolean getIsRecurring() {
		return this.parentRecurringJourneyId!=null || (this.journeyRecurrence != null && !this.journeyRecurrence.isEmpty());
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}
	
	public boolean parentRecurringJourney() {
		return (this.parentRecurringJourneyId == null && getIsRecurring());
	}
	
	public void removePairIdFromJourney(String pairId){
		Set<String> existingPairId = this.getJourneyPairIds();
		if(existingPairId != null && !existingPairId.isEmpty()){
			existingPairId.remove(pairId);
			if (existingPairId.isEmpty()) {
				existingPairId = null;
			}
		}
		return;
	}

	
	@DynamoDBIgnore
	public Boolean getHasAcceptedRequest() {
		return hasAcceptedRequest;
	}

	public void setHasAcceptedRequest(Boolean hasAcceptedRequest) {
		this.hasAcceptedRequest = hasAcceptedRequest;
	}


	public Boolean getFemaleOnlySearch() {
		return femaleOnlySearch;
	}

	public void setFemaleOnlySearch(Boolean femaleOnlySearch) {
		this.femaleOnlySearch = femaleOnlySearch;

	}

	@DynamoDBIgnore
	public Set<String> getIncomingJourneyPairIds() {
		return incomingJourneyPairIds;
	}

	public void setIncomingJourneyPairIds(Set<String> incomingJourneyPairIds) {
		this.incomingJourneyPairIds = incomingJourneyPairIds;
	}

	@DynamoDBIgnore
	public Set<String> getOutgoingJourneyPairIds() {
		return outgoingJourneyPairIds;
	}

	public void setOutgoingJourneyPairIds(Set<String> outgoingJourneyPairIds) {
		this.outgoingJourneyPairIds = outgoingJourneyPairIds;
	}
	
	public void addToIncomingPair(String pairId) {
		if(incomingJourneyPairIds == null) {
			incomingJourneyPairIds = new HashSet<String>();
		}
		incomingJourneyPairIds.add(pairId);
	}
	
	public void addToOutgoingPair(String pairId) {
		if(outgoingJourneyPairIds == null) {
			outgoingJourneyPairIds = new HashSet<String>();
		}
		outgoingJourneyPairIds.add(pairId);
	}
}
