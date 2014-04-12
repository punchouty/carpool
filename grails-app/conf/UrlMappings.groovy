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
			controller = "staticPage"
			action = "index"
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
		"/notifications"{
			controller = "journey"
			action = "myMatchedJourneys"
		}
		"/mlogin"{
			controller = "mobile"
			action = "login"
			parseRequest = true
		}
		"/mlogout"{
			controller = "mobile"
			action = "logout"
			parseRequest = true
		}
		"/msignup"{
			controller = "mobile"
			action = "signup"
			parseRequest = true
		}
		"/mpassword"{
			controller = "mobile"
			action = "changePassword"
			parseRequest = true
		}
		"/mforgot"{
			controller = "mobile"
			action = "forgotPassword"
			parseRequest = true
		}
		"/mprofile"{
			controller = "mobile"
			action = "getProfile"
			parseRequest = true
		}
		"/meditprofile"{
			controller = "mobile"
			action = "editProfile"
			parseRequest = true
		}
		"/msearch"{
			controller = "journey"
			action = "mobileFindMatching"
			parseRequest = true
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
