package com.racloop.domain;

public class WayPoint {
	
	public enum WayPointType {
		SOURCE,
		DESTINATION,
		PICKUP,
		DROP
	}
	
	public static final String SOURCE_MESSAGE = " will arrange cab/auto and start ride";
	public static final String DESTINATION_MESSAGE = " will settle fare and end ride";
	public static final String PICKUP_MESSAGE = " will board cab/auto";
	public static final String DROP_MESSAGE = " of current fare and get down";
	
	private final Double longitude;
	private final Double latitude;
	private final String place;
	private final String user;
	private final WayPointType type;
	
	private String recipient;
	private double percentFare;
	
	public WayPoint(Double latitude, Double longitude, String place, String user, WayPointType type) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.place = place;
		this.user = user;
		this.type = type;
	}

	public String getMessage() {
		String message = null;
		if(type == WayPointType.SOURCE) {
			message = this.user + SOURCE_MESSAGE;
		}
		else if(type == WayPointType.DESTINATION) {
			message = user + DESTINATION_MESSAGE;
		}
		else if(type == WayPointType.PICKUP) {
			message = user + PICKUP_MESSAGE;
		}
		else if(type == WayPointType.DROP) {
			message = user + " will pay" + recipient + " " + percentFare + DROP_MESSAGE;
		}
		return message;
	};
	
	@Override
	public boolean equals(Object obj) {
		WayPoint other = (WayPoint)obj;
		if(this.latitude == other.latitude && this.longitude == other.longitude) return true;
		return false;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public double getPercentFare() {
		return percentFare;
	}

	public void setPercentFare(double percentFare) {
		this.percentFare = percentFare;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public String getPlace() {
		return place;
	}

	public String getUser() {
		return user;
	}

	public WayPointType getType() {
		return type;
	}

}
