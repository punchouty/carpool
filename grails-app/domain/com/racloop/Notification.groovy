package com.racloop

class Notification {
	
//	Journey journey
//	User sender
//	User recipient
	String status	

    static constraints = {		
		status inList : ['Active', 'Rejected', 'Accepted', 'Inactive']
    }
}
