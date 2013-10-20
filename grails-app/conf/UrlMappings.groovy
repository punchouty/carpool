class UrlMappings {

	static mappings = {

		"/search" {
			controller = "staticPage"
			action = "search"
		}
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"{
			controller = "staticPage"
			action = "home"
		}
		"500"(view:'/error')
	}
}
