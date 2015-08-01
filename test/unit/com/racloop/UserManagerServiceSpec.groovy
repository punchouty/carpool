package com.racloop

import com.racloop.UserManagerService;

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(UserManagerService)
class UserManagerServiceSpec extends Specification {
    
	void testUserCode() {
		
		given: "Nothing"
			
		when: "service is called"
			
		then: "Expect userCode is generated"
			assert service.generateUserCode("Rohit").startsWith("ROHZ")
			assert service.generateUserCode("Rajan P").startsWith("RAJP")
			assert service.generateUserCode("R Arora").startsWith("RXXA")
			assert service.generateUserCode("R A").startsWith("RXXA")
			assert service.generateUserCode("Ro").startsWith("ROXZ")
			assert service.generateUserCode("Rohit K Arora").startsWith("ROHK")
			
    }
}
