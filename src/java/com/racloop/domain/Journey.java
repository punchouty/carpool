package com.racloop.domain;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.racloop.JourneyRequestCommand;

@DynamoDBTable(tableName = "Journey")
public class Journey {
	
	private String mobile;
	private Date dateOfJourney;
	private String email;
	private String name;
	private String esJourneyId;
	private Double fromLatitude;
	private Double fromLongitude;
	private String from;
	private Double toLatitude;
	private Double toLongitude;
	private String to;
	private Boolean isMale;
	private Boolean isDriver;
	private Double tripDistance;
	private String tripUnit;
	private String ip;
	private Date createdDate;
	private String photoUrl;
	
	//self and first
	// A -> B
	private String mobileFirst;
	private Date dateOfJourneyFirst;
	private String esJourneyIdFirst;
	private String statusSelfFirst; // self status w.r.t. request from first - INCOMING, OUTGOING, SEMI_INCOMING
	private String requestDirectionFirst; // INCOMING, OUTGOING, SEMI_INCOMING
	
	//self and second
	// A -> B and then A -> C || // A -> B and then B -> C
	private String mobileSecond;
	private Date dateOfJourneySecond;
	private String esJourneyIdSecond;
	private String statusSelfSecond; // self status w.r.t. request from second - INCOMING, OUTGOING, SEMI_INCOMING
	private String requestDirectionSecond; // INCOMING, OUTGOING, SEMI_INCOMING
	private Boolean isSelfOwner;
	
	private Boolean isDummy;
	private Long version;
	
	public JourneyRequestCommand convert() {
		JourneyRequestCommand journeyCommand = new JourneyRequestCommand();
		journeyCommand.setUser(email);
		journeyCommand.setMobile(mobile);
		journeyCommand.setId(esJourneyId);
		journeyCommand.setName(name);
		journeyCommand.setIsMale(isMale);
		journeyCommand.setDateOfJourney(dateOfJourney);
		journeyCommand.setFromPlace(from);
		journeyCommand.setFromLatitude(fromLatitude);
		journeyCommand.setFromLongitude(fromLongitude);
		journeyCommand.setToPlace(to);
		journeyCommand.setToLatitude(toLatitude);
		journeyCommand.setToLongitude(toLongitude);
		journeyCommand.setIsDriver(isDriver);
		journeyCommand.setTripDistance(tripDistance);
		journeyCommand.setIp(ip);
		journeyCommand.setCreatedDate(createdDate);
		journeyCommand.setPhotoUrl(photoUrl);
		return journeyCommand;
	}

	@Override 
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("mobile : " + mobile + ", ");
		buffer.append("dateOfJourney : " + dateOfJourney + ", ");
		buffer.append("email : " + email + ", ");
		buffer.append("esJourneyId : " + esJourneyId + ", ");
		buffer.append("name : " + name + ", ");
		buffer.append("from : " + from + ", ");
		buffer.append("to : " + to + ", ");
		buffer.append("mobileFirst : " + mobileFirst + ", ");
		buffer.append("mobileSecond : " + mobileSecond + ", ");
		return buffer.toString();
	}
	
	@DynamoDBHashKey(attributeName="Mobile") 
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = "DateOfJourney-Mobile-index", attributeName = "Mobile")
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@DynamoDBRangeKey(attributeName="DateOfJourney") 
	@DynamoDBIndexHashKey(globalSecondaryIndexName = "DateOfJourney-Mobile-index", attributeName = "DateOfJourney")
	public Date getDateOfJourney() {
		return dateOfJourney;
	}
	public void setDateOfJourney(Date dateOfJourney) {
		this.dateOfJourney = dateOfJourney;
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
	
	@DynamoDBAttribute(attributeName="EsJourneyId") 
	public String getEsJourneyId() {
		return esJourneyId;
	}
	public void setEsJourneyId(String esJourneyId) {
		this.esJourneyId = esJourneyId;
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
	
	@DynamoDBAttribute(attributeName="MobileFirst") 
	public String getMobileFirst() {
		return mobileFirst;
	}
	public void setMobileFirst(String mobileFirst) {
		this.mobileFirst = mobileFirst;
	}
	
	@DynamoDBAttribute(attributeName="DateOfJourneyFirst") 
	public Date getDateOfJourneyFirst() {
		return dateOfJourneyFirst;
	}
	public void setDateOfJourneyFirst(Date dateOfJourneyFirst) {
		this.dateOfJourneyFirst = dateOfJourneyFirst;
	}
	
	@DynamoDBAttribute(attributeName="EsJourneyIdFirst") 
	public String getEsJourneyIdFirst() {
		return esJourneyIdFirst;
	}
	public void setEsJourneyIdFirst(String esJourneyIdFirst) {
		this.esJourneyIdFirst = esJourneyIdFirst;
	}
	
	@DynamoDBAttribute(attributeName="StatusSelfFirst") 
	public String getStatusSelfFirst() {
		return statusSelfFirst;
	}
	public void setStatusSelfFirst(String statusSelfFirst) {
		this.statusSelfFirst = statusSelfFirst;
	}
	
	@DynamoDBAttribute(attributeName="RequestDirectionFirst") 
	public String getRequestDirectionFirst() {
		return requestDirectionFirst;
	}
	public void setRequestDirectionFirst(String requestDirectionFirst) {
		this.requestDirectionFirst = requestDirectionFirst;
	}
	
	@DynamoDBAttribute(attributeName="MobileSecond") 
	public String getMobileSecond() {
		return mobileSecond;
	}
	public void setMobileSecond(String mobileSecond) {
		this.mobileSecond = mobileSecond;
	}
	
	@DynamoDBAttribute(attributeName="DateOfJourneySecond") 
	public Date getDateOfJourneySecond() {
		return dateOfJourneySecond;
	}
	public void setDateOfJourneySecond(Date dateOfJourneySecond) {
		this.dateOfJourneySecond = dateOfJourneySecond;
	}
	
	@DynamoDBAttribute(attributeName="EsJourneyIdSecond") 
	public String getEsJourneyIdSecond() {
		return esJourneyIdSecond;
	}
	public void setEsJourneyIdSecond(String esJourneyIdSecond) {
		this.esJourneyIdSecond = esJourneyIdSecond;
	}
	
	@DynamoDBAttribute(attributeName="StatusSelfSecond") 
	public String getStatusSelfSecond() {
		return statusSelfSecond;
	}
	public void setStatusSelfSecond(String statusSelfSecond) {
		this.statusSelfSecond = statusSelfSecond;
	}
	
	@DynamoDBAttribute(attributeName="RequestDirectionSecond") 
	public String getRequestDirectionSecond() {
		return requestDirectionSecond;
	}
	public void setRequestDirectionSecond(String requestDirectionSecond) {
		this.requestDirectionSecond = requestDirectionSecond;
	}
	
	@DynamoDBAttribute(attributeName="IsSelfOwner") 
	public Boolean getIsSelfOwner() {
		return isSelfOwner;
	}
	public void setIsSelfOwner(Boolean isSelfOwner) {
		this.isSelfOwner = isSelfOwner;
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

}
