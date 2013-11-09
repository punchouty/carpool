package com.racloop;

import java.text.DecimalFormat;
import java.util.Random;

import org.elasticsearch.common.geo.GeoPoint;

public class DummyDataUtil {
	
	private static final double CONSTANT = 60 * 1.1515 * 1.609344;
	private static final double RADIUS_OF_EARTH = 6372.796924; // IN KM
	private static final Random randomGenerator1 = new Random();
	private static final Random randomGenerator2 = new Random();
	private static final DecimalFormat format = new DecimalFormat("##.######");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GeoPoint geoPoint = new GeoPoint(28.633123, 77.219540);
		GeoPoint geoPointReturn = random(geoPoint, 250);
		System.out.println(format.format(geoPointReturn.lat()) + "," + format.format(geoPointReturn.lon()));
		showSampleDistances();
	}

	private static void showSampleDistances() {
		double d = distance(28.633123, 77.219540, 30.798990,76.917072);
		System.out.println("Rajiv Chowk to Pinjore : " + d);
		d = distance(28.633123,77.219540, 30.747774, 76.794298);
		System.out.println("Rajiv Chowk to Chandigarh : " + d);
		d = distance(28.633123,77.219540, 31.129044, 77.231005);
		System.out.println("Rajiv Chowk to Mashorba : " + d);
		d = distance(28.633123,77.219540, 28.962041, 77.092510);
		System.out.println("Rajiv Chowk to Sonepat : " + d);
		d = distance(28.633123,77.219540, 28.477670, 77.068134);
		System.out.println("Rajiv Chowk to Gurgaon : " + d);
	}

	private static double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))	+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		//dist = dist * 60 * 1.1515 * 1.609344;
		dist = dist * CONSTANT;
		return dist;
	}
	
	public static GeoPoint random(GeoPoint geoPoint, double distance) {
		return random(geoPoint.lat(), geoPoint.lon(), distance);
	}
	
	public static GeoPoint random(double latOrigin, double lonOrigin, double distance) {
		double u = randomGenerator1.nextDouble();
		double v = randomGenerator2.nextDouble();
		double distanceInRadians = distance/RADIUS_OF_EARTH;
		double randomDistance = Math.acos(u * (Math.cos(distanceInRadians) - 1) + 1);
		double randomBearing = 2 * Math.PI * v;
		double latitudeRandom = Math.asin(Math.sin(deg2rad(latOrigin)) * Math.cos(randomDistance) + Math.cos(deg2rad(latOrigin)) * Math.sin(randomDistance) * Math.cos(randomBearing));
		double longitudeRandom = deg2rad(lonOrigin) + Math.atan2(Math.sin(randomBearing) * Math.sin(randomDistance) * Math.cos(deg2rad(latOrigin)), Math.cos(randomDistance) - Math.sin(deg2rad(latOrigin)) * Math.sin(latitudeRandom));
		if(longitudeRandom < -(Math.PI)) longitudeRandom = longitudeRandom + 2 * Math.PI;
		if(longitudeRandom > Math.PI) longitudeRandom = longitudeRandom - 2 * Math.PI;
		return new GeoPoint(rad2deg(latitudeRandom), rad2deg(longitudeRandom));
		
	}

	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

}
