class UrlMappings {
	static mappings = {
		"/contact/show/$contactId"(controller:'contact', action:'show') {}
		"/group/show/$groupId/contact?/$action?/$contactId?"(controller:'contact') {}

		"/message/inbox"(controller:'message', action:'inbox') {}
		"/message/inbox/show/$id"(controller:'message', action:'show') {
			messageSection = 'inbox'
		}

		"/message/sent"(controller:'message', action:'sent') {}
		"/message/sent/show/$id"(controller:'message', action:'show') {
			messageSection = 'sent'
		}

		"/message/poll/$pollId"(controller:'message', action:'poll') {}
		"/message/poll/$pollId/show/$id"(controller:'message', action:'show') {
			messageSection = 'poll'
		}

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
