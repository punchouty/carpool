package com.racloop.domain;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "UserRating")
public class Rating {

	private String reviewee;
	private String reviewer;
	private String comment;
	private Date dateCreated;
	private Double ettiquetes; // 1-5 scale
	private Double punctualty; // 1-5 scale
	private Double overall; // 1-5 scale
	
	@DynamoDBHashKey(attributeName="Reviewee") 
	public String getReviewee() {
		return reviewee;
	}
	public void setReviewee(String reviewee) {
		this.reviewee = reviewee;
	}
	
	@DynamoDBRangeKey(attributeName="Reviewer")
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	
	@DynamoDBAttribute(attributeName="Comment") 
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@DynamoDBIndexRangeKey(localSecondaryIndexName="DateCreated-index")
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	@DynamoDBAttribute(attributeName="Ettiquetes") 
	public Double getEttiquetes() {
		return ettiquetes;
	}
	public void setEttiquetes(Double ettiquetes) {
		this.ettiquetes = ettiquetes;
	}
	
	@DynamoDBAttribute(attributeName="Punctualty") 
	public Double getPunctualty() {
		return punctualty;
	}
	public void setPunctualty(Double punctualty) {
		this.punctualty = punctualty;
	}
	
	@DynamoDBAttribute(attributeName="Overall") 
	public Double getOverall() {
		return overall;
	}
	public void setOverall(Double overall) {
		this.overall = overall;
	}
	
	
}
