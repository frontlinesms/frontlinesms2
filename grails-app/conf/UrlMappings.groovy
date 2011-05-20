class UrlMappings {
	static mappings = {
		"/contact/show/$contactId"(controller:'contact', action:'show') {}

		"/group/show/$groupId/contact?/$action?/$contactId?"(controller:'contact') {}

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
