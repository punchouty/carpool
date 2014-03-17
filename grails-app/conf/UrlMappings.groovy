class UrlMappings {

	static mappings = {
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		
		"/safety"{
			controller = "staticPage"
			action = "safety"
		}
		
		"/faq"{
			controller = "staticPage"
			action = "faq"
		}

		"/signin"{
			controller = "userSession"
			action = "signin"
		}

		"/auth/login"{
			controller = "userSession"
			action = "signin"
		}

		"/signup"{
			controller = "userSession"
			action = "signup"
		}

		"/signout"{
			controller = "auth"
			action = "logout"
		}
		
		"/about"{
			controller = "staticPage"
			action = "about"
		}
		
		"/etiquettes"{
			controller = "staticPage"
			action = "etiquettes"
		}
		
		"/terms"{
			controller = "staticPage"
			action = "terms"
		}
		
		"/privacy"{
			controller = "staticPage"
			action = "privacy"
		}

		"/"{
			controller = "userSession"
			action = "search"
		}

		"/search" {
			controller = "userSession"
			action = "search"
		}

		"/profile" {
			controller = "userSession"
			action = "profile"
		}

		"/password/change"{
			controller = "userSession"
			action = "changePassword"
		}

		"/password/forgot"{
			controller = "userSession"
			acti
			on = "forgotPassword"
		}

		"/journeys" {
			controller = "journey"
			action = "activeJourneys"
		}
		
		"/history" {
			controller = "journey"
			action = "history"
		}
		
		"/requests"{
			controller = "journey"
			action = "myJourneys"
		}
		"/notifications"{
			controller = "journey"
			action = "myMatchedJourneys"
		}
		
		"500"(view:'/error')
		"404"(view:'/404')
		"403"(view:'/403')
	}
}
