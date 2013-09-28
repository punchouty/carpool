package com.racloop

class Notification {
	
//	User recipient
	String status	

    static constraints = {		
		status inList : ['Active', 'Rejected', 'Accepted', 'Inactive']
    }
}
