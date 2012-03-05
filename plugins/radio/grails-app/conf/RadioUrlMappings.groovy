class RadioUrlMappings extends CoreUrlMappings{

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:'radio', action:'inbox')
		
		"/message/getNewMessageCount"(controller:'radioShow', action:'getNewRadioMessageCount') {}
		"/message/radioShow"(controller:'radioShow', action:'radioShow'){}
		"/message/radioShow/$ownerId"(controller:'radioShow', action:'radioShow') {}
		"/message/radioShow/$ownerId/show/$messageId"(controller:'radioShow', action:'radioShow') {}
	}
}
