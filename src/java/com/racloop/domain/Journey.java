package com.racloop.domain;

import java.util.Date;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.racloop.JourneyRequestCommand;

@DynamoDBTable(tableName = "Journey")
public class Journey {

	private String id;
	private String mobile;
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
	private Double tripDistance;
	private String tripUnit;
	private String ip;
	private Date createdDate;
	private String photoUrl;
	private Set<String> journeyPairIds;
	private Integer numberOfCopassengers;
	private Boolean isSearchable;
	private Boolean isDummy;
	private Long version;
	
	public JourneyRequestCommand convert() {
		JourneyRequestCommand journeyCommand = new JourneyRequestCommand();
		journeyCommand.setId(id);
		journeyCommand.setUser(email);
		journeyCommand.setMobile(mobile);
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
		buffer.append("id : " + id + ", ");
		buffer.append("mobile : " + mobile + ", ");
		buffer.append("dateOfJourney : " + dateOfJourney + ", ");
		buffer.append("email : " + email + ", ");
		buffer.append("name : " + name + ", ");
		buffer.append("from : " + from + ", ");
		buffer.append("to : " + to + ", ");
		buffer.append("journeyPairIds : " + journeyPairIds + ", ");
		return buffer.toString();
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Journey other = (Journey)obj;
		return other.id.equals(this.id);
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
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = "DateOfJourney-Mobile-index", attributeName = "Mobile")
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@DynamoDBIndexHashKey(globalSecondaryIndexName = "DateOfJourney-Mobile-index", attributeName = "DateOfJourney")
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = "Mobile-DateOfJourney-index", attributeName = "DateOfJourney")
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

}
