class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:'message')
		
		"/message/radioShow/$ownerId"(controller:'message', action:'standard') {}
		"/message/radioShow/$ownerId/show/$messageId"(controller:'message', action:'standard') {}
		
		"500"(view:'/error')
	}
}
