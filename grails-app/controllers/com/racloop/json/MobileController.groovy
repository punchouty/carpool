package com.racloop.json

class MobileController {

    def login() { }
	
	def logout() { }
	
	def signup() { }
	
	def changePassword() { }
	
	def forgotPassword() { }
	
	//User 1
	def search() { }
	
	//User 1 - commercial driver
	def searchStartPoint() { }
	
	//User 1 - persist request for other to respond
	def addJourney() { }
	
	//User 1 - List of journeys
	def myJourneys() { }
	
	//User 1 - cancel then persisted request
	//Two use cases - 1. When some one responded 2. When no one responded
	def cancelJourney() { }
	
	//User 2 - response to matched journey
	def sendResponse() { }
	
	//User 2 - responses
	//Display phone depending upon status (ACCEPTED)
	//Cancel or status use cases also handled here
	def myOutgoingResponses() { }
	
	//User 1 - accept request
	// Display phone depending upon status (ACCEPTED)
	//Cancel or status use cases also handled here
	def myIncomingResponses() { }
	
	//User 1 - accept request
	def acceptResponse() { }
	
	//User 1 - reject request
	def rejectResponse() { }
	
	//User 2 - cancel earlier sent response
	def cancelResponse() { }
}
