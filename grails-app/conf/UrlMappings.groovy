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
			action = "forgotPassword"
		}
		"500"(view:'/error')
	}
}
