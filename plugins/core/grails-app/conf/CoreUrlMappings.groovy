class CoreUrlMappings {
	static mappings = {
		"/"(controller:'message')
		
		"/contact/show/$contactId?"(controller:'contact', action:'show') {}
		
		"/group/show/$groupId/contact/show/$contactId" {
			controller = 'contact'
			action = 'show'
		}
		
		"/search/no_search/$messageId"(controller:'search', action:'no_search') {}
		"/search/result/show/"(controller:'search', action:'result') {}
		"/search/result/show/$messageId"(controller:'search', action:'result') {}
		
		"/message/inbox/show/$messageId"(controller:'message', action:'inbox') {}
		"/message/sent/show/$messageId"(controller:'message', action:'sent') {}
		"/message/pending/show/$messageId"(controller:'message', action: 'pending') {}
		"/message/trash/show/$id"(controller:'message', action: 'trash') {}

		"/message/activity/$ownerId"(controller:'message', action:'activity') {}
		"/message/activity/$ownerId/show/$messageId"(controller:'message', action:'activity') {}

		"/message/folder/$ownerId"(controller:'message', action:'folder') {}
		"/message/folder/$ownerId/show/$messageId"(controller:'message', action:'folder') {}

		// Don't know why this is neccessary, but it is
		"/poll/create"(controller:'poll', action:'create')
		"/poll/save"(controller:'poll', action:'save')
		"/folder/create"(controller:'folder', action:'create')
		"/folder/save"(controller:'folder', action:'save')
		"/announcement/create"(controller:'announcement', action: 'create')
		"/announcement/save"(controller:'announcement', action: 'save')
		
		"/archive/inbox/show/$messageId"(controller:'archive', action:'inbox') {}
		"/archive/sent/show/$messageId"(controller:'archive', action:'sent') {}
		
		"/archive/poll"(controller:'archive', action:'activityList') {}
		"/archive/poll/$ownerId"(controller:'archive', action:'activityList') {}
		"/archive/poll/$ownerId/show/$messageId"(controller:'archive', action:'activityList') {}
		
		"/archive/announcement"(controller:'archive', action:'activityList') {}
		"/archive/announcement/$ownerId"(controller:'archive', action:'announcement') {}
		"/archive/announcement/$ownerId/show/$messageId"(controller:'archive', action:'announcement') {}
		
		"/archive/folder"(controller:'archive', action:'folderList') {}
		"/archive/folder/$ownerId"(controller:'archive', action:'folderList') {}
		"/archive/folder/$ownerId/show/$messageId"(controller:'archive', action:'folderList') {}
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"500"(view:'/error')
	}
}
