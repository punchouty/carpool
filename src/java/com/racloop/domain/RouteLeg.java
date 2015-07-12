package com.racloop.domain;

import com.racloop.DistanceUtil;

public class RouteLeg {
	
	private Double fromLatitude;
	private Double fromLongitude;
	private Double toLatitude;
	private Double toLongitude;
	private Double length;
	
	public RouteLeg(Double fromLatitude, Double fromLongitude,
			Double toLatitude, Double toLongitude) {
		this.fromLatitude = fromLatitude;
		this.fromLongitude = fromLongitude;
		this.toLatitude = toLatitude;
		this.toLongitude = toLongitude;
		this.length = DistanceUtil.distance(fromLatitude, fromLongitude, toLatitude, toLongitude);
	}
	
	public RouteLeg(Journey journey) {
		this.fromLatitude = journey.getFromLatitude();
		this.fromLongitude = journey.getFromLongitude();
		this.toLatitude = journey.getToLatitude();
		this.toLongitude = journey.getToLongitude();
		this.length = DistanceUtil.distance(this.fromLatitude, this.fromLongitude, this.toLatitude, this.toLongitude);
	}
	
	@Override
	public boolean equals(Object obj) {
		RouteLeg other  = (RouteLeg)obj;
		if(this.fromLatitude == other.fromLatitude && this.fromLongitude == other.fromLongitude && this.toLatitude == other.toLatitude && this.toLongitude == other.toLongitude) {
			return true;
		}
		return false;
	};

	public Double getFromLatitude() {
		return fromLatitude;
	}

	public Double getFromLongitude() {
		return fromLongitude;
	}

	public Double getToLatitude() {
		return toLatitude;
	}

	public Double getToLongitude() {
		return toLongitude;
	}

	public Double getLength() {
		return length;
	}

}
