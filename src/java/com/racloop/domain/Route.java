package com.racloop.domain;

import java.util.ArrayList;
import java.util.List;

import com.racloop.domain.WayPoint.WayPointType;


public class Route {
	
	private List<WayPoint> wayPoints = new ArrayList<>();

	public Route(Journey journey1, Journey journey2) {
		populateWayPoints(journey1, journey2);
	}
	
	public Route(Journey journey1, Journey journey2, Journey journey3) {
		populateWayPoints(journey1, journey2, journey3);
	}

	private void populateWayPoints(Journey journey1, Journey journey2) {
		RouteLeg journeyOneLeg = new RouteLeg(journey1);
		RouteLeg journeyTwoLeg = new RouteLeg(journey2);
		RouteLeg journeyOneStartJourneyTwoEndLeg = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
		RouteLeg journeyTwoStartJourneyOneEndLeg = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
		
		WayPoint one = null;
		WayPoint two = null;
		WayPoint three = null;
		WayPoint four = null;
		
		RouteLeg shortestLeg = journeyOneLeg; // Journey 1 Shortest
		if(shortestLeg.getLength() > journeyTwoLeg.getLength()) { // Journey 2 Shortest
			shortestLeg = journeyTwoLeg;
		}
		if(shortestLeg.getLength() > journeyOneStartJourneyTwoEndLeg.getLength()) {
			shortestLeg = journeyOneStartJourneyTwoEndLeg;
		}
		if(shortestLeg.getLength() > journeyTwoStartJourneyOneEndLeg.getLength()) {
			shortestLeg = journeyTwoStartJourneyOneEndLeg;
		}
		if(shortestLeg.equals(journeyOneLeg)) { //Journey 1 is shortest
			one = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.SOURCE);
			two = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			three = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DROP);
			four = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DESTINATION);
//			RouteLeg oneTwo = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
//			double totalDistance = shortestLeg.getLength() + oneTwo.getLength();
//			double percentFare = shortestLeg.getLength()/totalDistance;
			double percentFare = 0.5;
			three.setPercentFare(percentFare);
			three.setRecipient(journey2.getName());
		}
		else if(shortestLeg.equals(journeyTwoLeg)) { //Journey 2 is shortest
			one = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.SOURCE);
			two = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			three = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DROP);
			four = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DESTINATION);
//			RouteLeg oneTwo = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey2.getFromLatitude(), journey2.getFromLongitude());
//			double totalDistance = shortestLeg.getLength() + oneTwo.getLength();
//			double percentFare = shortestLeg.getLength()/totalDistance;
			double percentFare = 0.5;
			three.setPercentFare(percentFare);
			three.setRecipient(journey1.getName());
			
		}
		else if(shortestLeg.equals(journeyOneStartJourneyTwoEndLeg)) { // overlap
			one = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.SOURCE);
			two = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			three = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DROP);
			four = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DESTINATION);
//			RouteLeg oneTwo = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
//			double totalDistance = shortestLeg.getLength() + oneTwo.getLength();
//			double percentFare = journeyTwoLeg.getLength()/totalDistance;
			double percentFare = 0.5;
			three.setPercentFare(percentFare);
			three.setRecipient(journey1.getName());
		}
		else if(shortestLeg.equals(journeyTwoStartJourneyOneEndLeg)) { // overlap
			one = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.SOURCE);
			two = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			three = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DROP);
			four = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DESTINATION);
//			RouteLeg oneTwo = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey2.getFromLatitude(), journey2.getFromLongitude());
//			double totalDistance = shortestLeg.getLength() + oneTwo.getLength();
//			double percentFare = journeyOneLeg.getLength()/totalDistance;
			double percentFare = 0.5;
			three.setPercentFare(percentFare);
			three.setRecipient(journey2.getName());
		}
		else {
			throw new IllegalStateException("Should not come here");
		}
		wayPoints.add(one);
		wayPoints.add(two);
		wayPoints.add(three);
		wayPoints.add(four);
	}
	
	private void populateWayPoints(Journey journey1, Journey journey2, Journey journey3) {
		double percentFare = 0.33;
		RouteLeg journeyOneLeg = new RouteLeg(journey1);
		RouteLeg journeyTwoLeg = new RouteLeg(journey2);
		RouteLeg journeyThreeLeg = new RouteLeg(journey3);
		
		RouteLeg journeyOneStartJourneyTwoEndLeg = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
		RouteLeg journeyOneStartJourneyThreeEndLeg = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
		
		RouteLeg journeyTwoStartJourneyOneEndLeg = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
		RouteLeg journeyTwoStartJourneyThreeEndLeg = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
		
		RouteLeg journeyThreeStartJourneyOneEndLeg = new RouteLeg(journey3.getFromLatitude(), journey3.getFromLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
		RouteLeg journeyThreeStartJourneyTwoEndLeg = new RouteLeg(journey3.getFromLatitude(), journey3.getFromLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
		
		RouteLeg shortestLeg = journeyOneLeg; // Journey 1 Shortest
		if(shortestLeg.getLength() > journeyTwoLeg.getLength()) { // Journey 2 Shortest
			shortestLeg = journeyTwoLeg;
		}
		if(shortestLeg.getLength() > journeyThreeLeg.getLength()) { // Journey 3 Shortest
			shortestLeg = journeyThreeLeg;
		}
		if(shortestLeg.getLength() > journeyOneStartJourneyTwoEndLeg.getLength()) {
			shortestLeg = journeyOneStartJourneyTwoEndLeg;
		}
		if(shortestLeg.getLength() > journeyOneStartJourneyThreeEndLeg.getLength()) {
			shortestLeg = journeyOneStartJourneyThreeEndLeg;
		}
		if(shortestLeg.getLength() > journeyTwoStartJourneyOneEndLeg.getLength()) {
			shortestLeg = journeyTwoStartJourneyOneEndLeg;
		}
		if(shortestLeg.getLength() > journeyTwoStartJourneyThreeEndLeg.getLength()) {
			shortestLeg = journeyTwoStartJourneyThreeEndLeg;
		}
		if(shortestLeg.getLength() > journeyThreeStartJourneyOneEndLeg.getLength()) {
			shortestLeg = journeyThreeStartJourneyOneEndLeg;
		}
		if(shortestLeg.getLength() > journeyThreeStartJourneyTwoEndLeg.getLength()) {
			shortestLeg = journeyThreeStartJourneyTwoEndLeg;
		}
		
		WayPoint one = null;
		WayPoint two = null;
		WayPoint three = null;
		WayPoint four = null;
		WayPoint five = null;
		WayPoint six = null;
		
		if(shortestLeg.equals(journeyOneLeg)) { //Journey 1 is shortest
			three = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getTo(), journey1.getName(), WayPointType.DROP);
			
			// 1.3 - Journey 1 as three
			RouteLeg j2j1From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			RouteLeg j3j1From = new RouteLeg(journey3.getFromLatitude(), journey3.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			RouteLeg j2j3From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			double j2j3j1From = j2j3From.getLength() + j3j1From.getLength();
			double j3j2j1From = j2j3From.getLength() + j2j1From.getLength();
			if(j2j3j1From < j3j2j1From) {
				one = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			}
			
			// 1.4 - Journey 1 as four
			RouteLeg j1j2To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
			RouteLeg j1j3To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			RouteLeg j2j3To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			double j1j2j3To = j1j2To.getLength() + j2j3To.getLength();
			double j1j3j2To = j1j3To.getLength() + j2j3To.getLength();
			if(j1j2j3To < j1j3j2To) {
				five = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DROP);
				six = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey3.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey3.getName());
			}
			else {
				five = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DROP);
				six = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey2.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey2.getName());
			}
		}
		else if(shortestLeg.equals(journeyTwoLeg)) { //Journey 2 is shortest
			three = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getTo(), journey2.getName(), WayPointType.DROP);
			
			// 2.3 - Journey 2 as three
			RouteLeg j1j2From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey2.getFromLatitude(), journey2.getFromLongitude());
			RouteLeg j3j2From = new RouteLeg(journey3.getFromLatitude(), journey3.getFromLongitude(), journey2.getFromLatitude(), journey2.getFromLongitude());
			RouteLeg j1j3From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			double j1j3j2From = j1j3From.getLength() + j3j2From.getLength();
			double j3j1j2From = j1j3From.getLength() + j1j2From.getLength();
			if(j1j3j2From < j3j1j2From) {
				one = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			}
			
			// 2.4 - Journey 2 as four
			RouteLeg j2j1To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			RouteLeg j2j3To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			RouteLeg j1j3To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			double j2j1j3To = j2j1To.getLength() + j2j3To.getLength();
			double j2j3j1To = j2j3To.getLength() + j1j3To.getLength();
			if(j2j1j3To < j2j3j1To) {
				five = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DROP);
				six = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey3.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey3.getName());
			}
			else {
				five = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DROP);
				six = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey1.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey1.getName());
			}
			
		}
		else if(shortestLeg.equals(journeyThreeLeg)) { //Journey 3 is shortest
			three = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getTo(), journey3.getName(), WayPointType.DROP);
			
			// 3.3 - Journey 3 as three
			RouteLeg j1j3From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			RouteLeg j2j3From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			RouteLeg j2j1From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			double j1j2j3From = j2j1From.getLength() + j2j3From.getLength();
			double j2j1j3From = j2j1From.getLength() + j1j3From.getLength();
			if(j1j2j3From < j2j1j3From) {
				one = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			}
			
			// 3.4 - Journey 3 as four
			RouteLeg j3j2To = new RouteLeg(journey3.getToLatitude(), journey3.getToLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
			RouteLeg j3j1To = new RouteLeg(journey3.getToLatitude(), journey3.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			RouteLeg j2j1To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			double j3j1j2To = j3j1To.getLength() + j2j1To.getLength();
			double j3j2j1To = j3j2To.getLength() + j2j1To.getLength();
			if(j3j1j2To < j3j2j1To) {
				five = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DROP);
				six = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey2.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey2.getName());
			}
			else {
				five = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DROP);
				six = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey1.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey1.getName());
			}
			
		}
		else if(shortestLeg.equals(journeyOneStartJourneyTwoEndLeg)) { // overlap
			three = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getTo(), journey2.getName(), WayPointType.DROP);
			
			// Journey 1 as three - Refer 1.3
			RouteLeg j2j1From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			RouteLeg j3j1From = new RouteLeg(journey3.getFromLatitude(), journey3.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			RouteLeg j2j3From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			double j2j3j1From = j2j3From.getLength() + j3j1From.getLength();
			double j3j2j1From = j2j3From.getLength() + j2j1From.getLength();
			if(j2j3j1From < j3j2j1From) {
				one = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			}
			
			// Journey 2 as four - Refer 2.4
			RouteLeg j2j1To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			RouteLeg j2j3To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			RouteLeg j1j3To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			double j2j1j3To = j2j1To.getLength() + j2j3To.getLength();
			double j2j3j1To = j2j3To.getLength() + j1j3To.getLength();
			if(j2j1j3To < j2j3j1To) {
				five = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DROP);
				six = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey3.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey3.getName());
			}
			else {
				five = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DROP);
				six = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey1.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey1.getName());
			}
			
		}
		else if(shortestLeg.equals(journeyOneStartJourneyThreeEndLeg)) { // overlap
			three = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getTo(), journey3.getName(), WayPointType.DROP);
			
			// Journey 1 as three - Refer 1.3
			RouteLeg j2j1From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			RouteLeg j3j1From = new RouteLeg(journey3.getFromLatitude(), journey3.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			RouteLeg j2j3From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			double j2j3j1From = j2j3From.getLength() + j3j1From.getLength();
			double j3j2j1From = j2j3From.getLength() + j2j1From.getLength();
			if(j2j3j1From < j3j2j1From) {
				one = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			}
			
			// Journey 3 as four - Refer - 3.4
			RouteLeg j3j2To = new RouteLeg(journey3.getToLatitude(), journey3.getToLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
			RouteLeg j3j1To = new RouteLeg(journey3.getToLatitude(), journey3.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			RouteLeg j2j1To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			double j3j1j2To = j3j1To.getLength() + j2j1To.getLength();
			double j3j2j1To = j3j2To.getLength() + j2j1To.getLength();
			if(j3j1j2To < j3j2j1To) {
				five = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DROP);
				six = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey2.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey2.getName());
			}
			else {
				five = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DROP);
				six = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey1.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey1.getName());
			}
			
		}
		else if(shortestLeg.equals(journeyTwoStartJourneyOneEndLeg)) { // overlap
			three = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getTo(), journey1.getName(), WayPointType.DROP);
			
			// Journey 2 as three - Refer - 2.3
			RouteLeg j1j2From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey2.getFromLatitude(), journey2.getFromLongitude());
			RouteLeg j3j2From = new RouteLeg(journey3.getFromLatitude(), journey3.getFromLongitude(), journey2.getFromLatitude(), journey2.getFromLongitude());
			RouteLeg j1j3From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			double j1j3j2From = j1j3From.getLength() + j3j2From.getLength();
			double j3j1j2From = j1j3From.getLength() + j1j2From.getLength();
			if(j1j3j2From < j3j1j2From) {
				one = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			}
			
			// Journey 1 as four - Refer - 1.4
			RouteLeg j1j2To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
			RouteLeg j1j3To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			RouteLeg j2j3To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			double j1j2j3To = j1j2To.getLength() + j2j3To.getLength();
			double j1j3j2To = j1j3To.getLength() + j2j3To.getLength();
			if(j1j2j3To < j1j3j2To) {
				five = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DROP);
				six = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey3.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey3.getName());
			}
			else {
				five = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DROP);
				six = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey2.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey2.getName());
			}
			
		}
		else if(shortestLeg.equals(journeyTwoStartJourneyThreeEndLeg)) { // overlap
			three = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getTo(), journey3.getName(), WayPointType.DROP);
			
			// Journey 2 as three - Refer - 2.3
			RouteLeg j1j2From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey2.getFromLatitude(), journey2.getFromLongitude());
			RouteLeg j3j2From = new RouteLeg(journey3.getFromLatitude(), journey3.getFromLongitude(), journey2.getFromLatitude(), journey2.getFromLongitude());
			RouteLeg j1j3From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			double j1j3j2From = j1j3From.getLength() + j3j2From.getLength();
			double j3j1j2From = j1j3From.getLength() + j1j2From.getLength();
			if(j1j3j2From < j3j1j2From) {
				one = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			}
			
			// Journey 3 as four - Refer - 3.4
			RouteLeg j3j2To = new RouteLeg(journey3.getToLatitude(), journey3.getToLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
			RouteLeg j3j1To = new RouteLeg(journey3.getToLatitude(), journey3.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			RouteLeg j2j1To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			double j3j1j2To = j3j1To.getLength() + j2j1To.getLength();
			double j3j2j1To = j3j2To.getLength() + j2j1To.getLength();
			if(j3j1j2To < j3j2j1To) {
				five = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DROP);
				six = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey2.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey2.getName());
			}
			else {
				five = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DROP);
				six = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey1.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey1.getName());
			}
			
		}
		else if(shortestLeg.equals(journeyThreeStartJourneyOneEndLeg)) { // overlap
			three = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getTo(), journey1.getName(), WayPointType.DROP);
			
			// Journey 3 as three - Refer - 3.3
			RouteLeg j1j3From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			RouteLeg j2j3From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			RouteLeg j2j1From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			double j1j2j3From = j2j1From.getLength() + j2j3From.getLength();
			double j2j1j3From = j2j1From.getLength() + j1j3From.getLength();
			if(j1j2j3From < j2j1j3From) {
				one = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			}
			
			// Journey 1 as four - Refer - 1.4
			RouteLeg j1j2To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey2.getToLatitude(), journey2.getToLongitude());
			RouteLeg j1j3To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			RouteLeg j2j3To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			double j1j2j3To = j1j2To.getLength() + j2j3To.getLength();
			double j1j3j2To = j1j3To.getLength() + j2j3To.getLength();
			if(j1j2j3To < j1j3j2To) {
				five = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DROP);
				six = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey3.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey3.getName());
			}
			else {
				five = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DROP);
				six = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey2.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey2.getName());
			}
			
		}
		else if(shortestLeg.equals(journeyThreeStartJourneyTwoEndLeg)) { // overlap
			three = new WayPoint(journey3.getFromLatitude(), journey3.getFromLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.PICKUP);
			four = new WayPoint(journey2.getToLatitude(), journey2.getToLongitude(), journey2.getTo(), journey2.getName(), WayPointType.DROP);
			
			// Journey 3 as three - Refer - 3.3
			RouteLeg j1j3From = new RouteLeg(journey1.getFromLatitude(), journey1.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			RouteLeg j2j3From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey3.getFromLatitude(), journey3.getFromLongitude());
			RouteLeg j2j1From = new RouteLeg(journey2.getFromLatitude(), journey2.getFromLongitude(), journey1.getFromLatitude(), journey1.getFromLongitude());
			double j1j2j3From = j2j1From.getLength() + j2j3From.getLength();
			double j2j1j3From = j2j1From.getLength() + j1j3From.getLength();
			if(j1j2j3From < j2j1j3From) {
				one = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.PICKUP);
			}
			else {
				one = new WayPoint(journey2.getFromLatitude(), journey2.getFromLongitude(), journey2.getFrom(), journey2.getName(), WayPointType.SOURCE);
				two = new WayPoint(journey1.getFromLatitude(), journey1.getFromLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.PICKUP);
			}
			
			// Journey 2 as four - Refer 2.4
			RouteLeg j2j1To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey1.getToLatitude(), journey1.getToLongitude());
			RouteLeg j2j3To = new RouteLeg(journey2.getToLatitude(), journey2.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			RouteLeg j1j3To = new RouteLeg(journey1.getToLatitude(), journey1.getToLongitude(), journey3.getToLatitude(), journey3.getToLongitude());
			double j2j1j3To = j2j1To.getLength() + j2j3To.getLength();
			double j2j3j1To = j2j3To.getLength() + j1j3To.getLength();
			if(j2j1j3To < j2j3j1To) {
				five = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DROP);
				six = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey3.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey3.getName());
			}
			else {
				five = new WayPoint(journey3.getToLatitude(), journey3.getToLongitude(), journey3.getFrom(), journey3.getName(), WayPointType.DROP);
				six = new WayPoint(journey1.getToLatitude(), journey1.getToLongitude(), journey1.getFrom(), journey1.getName(), WayPointType.DESTINATION);
				four.setPercentFare(percentFare);
				four.setRecipient(journey1.getName());
				five.setPercentFare(percentFare);
				five.setRecipient(journey1.getName());
			}
			
		}
		else {
			throw new IllegalStateException("Should not come here");
		}
		wayPoints.add(one);
		wayPoints.add(two);
		wayPoints.add(three);
		wayPoints.add(four);
		wayPoints.add(five);
		wayPoints.add(six);
	}

}
