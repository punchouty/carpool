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

		"/login"{
			controller = "userSession"
			action = "login"
		}

		"/auth/login"{
			controller = "userSession"
			action = "signin"
		}

		"/logout" {
			controller = "auth"
			action = "logout"
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
		
		"/verifyMobile"{
			controller = "userSession"
			action = "verifyMobile"
		}

		"/main"{
			controller = "staticPage"
			action = "main"
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
			action = "forgotPassword"
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
		
		"/health"(view:"/health")
		
		"500"{
			controller = "error"
			action = "internalError"
		}
		"404"{
			controller = "error"
			action = "notFound"
		}
		"403"{
			controller = "error"
			action = "forbidden"
		}
	}
}
