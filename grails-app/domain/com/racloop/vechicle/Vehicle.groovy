package com.racloop.vechicle

import com.racloop.User;

// Use http://java.dzone.com/articles/grails-dynamic-dropdown

class Vehicle {
	
	String vehicleRegisteration
	Model model
	String color
	static belongsTo = User

    static constraints = {
    }
}
