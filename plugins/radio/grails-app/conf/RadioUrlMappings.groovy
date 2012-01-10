class RadioUrlMappings extends CoreUrlMappings{

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:'message')
		
		"/message/getNewMessageCount"(controller:'radioShow', action:'getNewRadioMessageCount') {}
		"/message/radioShow"(controller:'radioShow', action:'radioShow'){}
		"/message/radioShow/$ownerId"(controller:'radioShow', action:'radioShow') {}
		"/message/radioShow/$ownerId/show/$messageId"(controller:'radioShow', action:'radioShow') {}
		
		"500"(view:'/error')
	}
}
