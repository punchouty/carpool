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
import grails.plugin.nimble.core.AdminsService
import grails.plugin.nimble.security.NimbleFilterBase

/**
 * Filter that works with Nimble security model to protect controllers, actions, views
 *
 * @author Bradley Beddoes
 */
class SecurityFilters extends NimbleFilterBase {

	def filters = {		
		journeysecure(controller: "journey", action: "(newJourney|requestService|getWorkflow)") {
			before = { accessControl { true } }
		}
		
//		homesecure(controller: "staticPage", action: "search") {
//			before = { accessControl { true } }
//		}
		
		// Account management requiring authentication
		accountsecure(controller: "userSession", action: "(changePassword|updatePassword|changedPassword|profile)") {
			before = { accessControl { true } }
		}
		
//		otheradminsecure(controller: "staticData|sampleData") {
//			accessControl { role(AdminsService.ADMIN_ROLE) }
//		}
	}
}
