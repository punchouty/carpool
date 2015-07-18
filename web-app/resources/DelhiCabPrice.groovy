returnMap =[:]
returnMap.put("UberX", getUberXPrice(distanceInKm, timeInSeconds))
returnMap.put("UberGo", getUberGoPrice(distanceInKm, timeInSeconds))
returnMap.put("UberBlack", getUberBlackPrice(distanceInKm, timeInSeconds))
returnMap.put("olaEconomy", getOlaEconomyPrice(distanceInKm, timeInSeconds))
returnMap.put("olaMini", getOlaMiniPrice(distanceInKm, timeInSeconds))
returnMap.put("olaPrime", getOlaPrimePrice(distanceInKm, timeInSeconds))
returnMap.put("meruCab", getMeruCabFares(distanceInKm, timeInSeconds))
returnMap.put("easyCab", getEasyCabFares(distanceInKm, timeInSeconds))
returnMap.put("megaCab", getMegaCabFares(distanceInKm, timeInSeconds))

private static Double getUberGoPrice (Double distanceInKm, Integer timeInSeconds) {
	int baseFare = 40
	int ratePerMin = 1
	int ratePerKm = 7
	return baseFare + distanceInKm*ratePerKm + timeInSeconds*ratePerMin/60
}

private static Double getUberBlackPrice (Double distanceInKm, Integer timeInSeconds) {
	int baseFare = 85
	double ratePerMin = 1.5
	double ratePerKm = 12
	return baseFare + distanceInKm*ratePerKm + timeInSeconds*ratePerMin/60
}

private static Double getUberXPrice (Double distanceInKm, Integer timeInSeconds) {
	int baseFare = 55
	double ratePerMin = 1
	double ratePerKm = 9
	return baseFare + distanceInKm*ratePerKm + timeInSeconds*ratePerMin/60
}
private static Double getOlaEconomyPrice (Double distanceInKm, Integer timeInSeconds) {
	double ratePerKm = 11
	double ratePerMin = 1
	double distanceForMinBill = 4
	double totalFare = 100
	double distance = distanceInKm - distanceForMinBill
	if(distance>0) {
		totalFare = totalFare + ratePerKm * distance
	}
	if(timeInSeconds>300){
		totalFare = totalFare + (timeInSeconds - 300)*ratePerMin/60
	}
	return totalFare
}
private static Double getOlaMiniPrice (Double distanceInKm, Integer timeInSeconds) {
	double ratePerKm = 8
	double ratePerMin = 1
	double distanceForMinBill = 4
	double totalFare = 100
	int timeMarkupInSeconds = 300
	double distance = distanceInKm - distanceForMinBill
	if(distance>0) {
		totalFare = totalFare + ratePerKm * distance
	}
	if(timeInSeconds>timeMarkupInSeconds){
		totalFare = totalFare + (timeInSeconds - timeMarkupInSeconds)*ratePerMin/60
	}
	return totalFare
}

private static Double getOlaPrimePrice (Double distanceInKm, Integer timeInSeconds) {
	double ratePerKm = 20
	double ratePerMin = 2
	double distanceForMinBill = 5
	double totalFare = 200
	int timeMarkupInSeconds = 0
	double distance = distanceInKm - distanceForMinBill
	if(distance>0) {
		totalFare = totalFare + ratePerKm * distance
	}
	if(timeInSeconds>timeMarkupInSeconds){
		totalFare = totalFare + (timeInSeconds - timeMarkupInSeconds)*ratePerMin/60
	}
	return totalFare
}

private static Double getMeruCabFares (Double distanceInKm, Integer timeInSeconds) {
	double ratePerKm = 23
	double ratePerMin = 1
	double distanceForMinBill = 3
	double totalFare = 69
	int timeMarkupInSeconds = 15*60
	double distance = distanceInKm - distanceForMinBill
	if(distance>0) {
		totalFare = totalFare + ratePerKm * distance
	}
	if(timeInSeconds>timeMarkupInSeconds){
		totalFare = totalFare + (timeInSeconds - timeMarkupInSeconds)*ratePerMin/60
	}
	return totalFare
}

private static Double getEasyCabFares (Double distanceInKm, Integer timeInSeconds) {
	double ratePerKm = 23
	double ratePerMin = 30/60
	double distanceForMinBill = 3
	double totalFare = 69
	int timeMarkupInSeconds = 0
	double distance = distanceInKm - distanceForMinBill
	if(distance>0) {
		totalFare = totalFare + ratePerKm * distance
	}
	if(timeInSeconds>timeMarkupInSeconds){
		totalFare = totalFare + (timeInSeconds - timeMarkupInSeconds)*ratePerMin/60
	}
	return totalFare
}

private static Double getMegaCabFares (Double distanceInKm, Integer timeInSeconds) {
	double ratePerKm = 23
	double ratePerMin = 0
	double distanceForMinBill = 3
	double totalFare = 69
	int timeMarkupInSeconds = 0
	double distance = distanceInKm - distanceForMinBill
	if(distance>0) {
		totalFare = totalFare + ratePerKm * distance
	}
	if(timeInSeconds>timeMarkupInSeconds){
		totalFare = totalFare + (timeInSeconds - timeMarkupInSeconds)*ratePerMin/60
	}
	return totalFare
}
return returnMap