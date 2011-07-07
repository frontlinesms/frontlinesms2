class UrlMappings {
	static mappings = {
		"/contact/show/$contactId"(controller:'contact', action:'show') {}

		"/group/show/$groupId/contact/show/$contactId" {
			controller = 'contact'
			action = 'show'
		}
		
		"/message/inbox/show/$messageId"(controller:'message', action:'inbox') {}

		"/message/poll/$ownerId"(controller:'message', action:'poll') {}
		"/message/poll/$ownerId/show/$messageId"(controller:'message', action:'poll') {}

		"/folder/$ownerId"(controller:'message', action:'folder') {}
		"/message/folder/$ownerId/show/$messageId"(controller:'message', action:'folder') {}

		// Don't know why this is neccessary, but it is
		"/poll/create"(controller:'poll', action:'create')
		"/poll/save"(controller:'poll', action:'save')
		"/folder/create"(controller:'folder', action:'create')
		"/folder/save"(controller:'folder', action:'save')

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
