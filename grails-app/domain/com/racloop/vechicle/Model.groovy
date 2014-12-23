package com.racloop.vechicle

class Model {
	
	String name
	int passangerSeats = 3
	String comfortLevel
	
	static belongsTo = Make

    static constraints = {
    }
	
	String toString() {
		name
	}
}
