package com.racloop

class Notification {
	
	User sender
	String status	

    static constraints = {		
		status inList : ['Active', 'Rejected', 'Accepted', 'Inactive']
    }
}
