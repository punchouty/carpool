package com.racloop

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class UserTestSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void shouldGiveUserWorkflowIndexName() {
		when:
		User user = new User(username:'rohit@racloop.com')
		then:
		user.getUserWorkflowIndexName() == 'USER_WORKFLOW_6817'
		when:
		user = new User(username:'rarora@gmail.com')
		then:
		user.getUserWorkflowIndexName() == 'USER_WORKFLOW_5437'
    }
	
	void shouldThrowExceptionWhenUserIsNull(){
		when:
		User user = new User()
		user.getUserWorkflowIndexName()
		then:
		thrown(NullPointerException)
	}
}
