/*
 *  Nimble, an extensive application base for Grails
 *  Copyright (C) 2010 Bradley Beddoes
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import grails.plugin.facebooksdk.FacebookContext
import grails.plugin.nimble.security.NimbleFilterBase

import org.codehaus.groovy.grails.web.util.WebUtils

import com.racloop.User

/**
 * Filter that works with Nimble security model to protect controllers, actions, views. Overwritten from Nimble
 *
 */
class SecurityFilters extends NimbleFilterBase {
	
	FacebookContext facebookContextProxy

	def filters = {	
		def clos = { true}
		journeysecure(controller: "journey", action: "(newJourney|requestService|getWorkflow|history|activeJourneys)") {
			before = { 
				if (facebookContextProxy.app.id && facebookContextProxy.authenticated) {
					User user  = User.findByFacebookId(facebookContextProxy.user.id.toString())
					if(user) {
						return true
					}
					
				}
				accessControl(auth: false, clos) 
			}
		}
		
//		homesecure(controller: "staticPage", action: "search") {
//			before = { accessControl { true } }
//		}
		
		// Account management requiring authentication
		accountsecure(controller: "userSession", action: "(changePassword|updatePassword|changedPassword|profile|editProfile)") {
			before = { 
				if (facebookContextProxy.app.id && facebookContextProxy.authenticated) {
					User user  = User.findByFacebookId(facebookContextProxy.user.id.toString())
					if(user) {
						return true
					}
					
				}
				accessControl(auth: false, clos) 
			}
		}
		
//		otheradminsecure(controller: "staticData|sampleData") {
//			accessControl { role(AdminsService.ADMIN_ROLE) }
//		}
	}
	
	def onNotAuthenticated(subject, filter) {
		def grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
		// request is the HttpServletRequest
		def flash = grailsWebRequest.attributes.getFlashScope(filter.request)
		flash.message = "Please sign in to continue."
		super.onNotAuthenticated(subject, filter)
	}
}
