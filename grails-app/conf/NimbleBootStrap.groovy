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

import grails.plugin.nimble.InstanceGenerator
import grails.plugin.nimble.core.AdminsService
import grails.plugin.nimble.core.Role
import grails.plugin.nimble.core.UserBase

/*
 * Allows applications using Nimble to undertake process at BootStrap that are related to Nimbe provided objects
 * such as Users, Role, Groups, Permissions etc.
 *
 * Utilizing this BootStrap class ensures that the Nimble environment is populated in the backend data repository correctly
 * before the application attempts to make any extenstions.
 */
class NimbleBootStrap {

	def grailsApplication

	def nimbleService
	def userService
	def adminsService

	def init = {servletContext ->

		// The following must be executed
		//internalBootStap(servletContext)

		// Execute any custom Nimble related BootStrap for your application below
	}

//	private internalBootStap(servletContext) {
//		nimbleService.init()
//	}
}
