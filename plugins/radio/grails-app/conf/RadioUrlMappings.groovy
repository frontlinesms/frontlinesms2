class RadioUrlMappings extends CoreUrlMappings{

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:'message')
		
		"/radioShow/$ownerId"(controller:'radioShow', action:'radioShow') {}
		"/radioShow/$ownerId/show/$messageId"(controller:'radioShow', action:'radioShow') {}
		
		"500"(view:'/error')
	}
}
