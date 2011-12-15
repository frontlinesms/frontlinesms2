class UrlMappings {
	static mappings = {
		"/"(controller:'message')
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"500"(view:'/error')
	}
}
