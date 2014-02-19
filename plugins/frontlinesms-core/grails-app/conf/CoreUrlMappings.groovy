class CoreUrlMappings {
	static mappings = {
		"/"(controller:'message')
		
		"/contact/show/$contactId?"(controller:'contact', action:'show') {}
		
		"/group/show/$groupId/contact/show/$contactId" {
			controller = 'contact'
			action = 'show'
		}
		
		"/search/no_search/$interactionId?"(controller:'search', action:'no_search') {}
		"/search/result/show/"(controller:'search', action:'result') {}
		"/search/result/show/$interactionId"(controller:'search', action:'result') {}
		
		"/message/inbox/"(controller:'message', action:'inbox') {}
		"/message/inbox/show/$interactionId"(controller:'message', action:'inbox') {}

		"/message/sent/show/$interactionId"(controller:'message', action:'sent') {}
		"/message/pending/show/$interactionId"(controller:'message', action: 'pending') {}
		"/message/trash/show/$id"(controller:'message', action: 'trash') {}

		"/message/activity/$ownerId"(controller:'message', action:'activity') {}
		"/message/activity/$ownerId/step/$stepId"(controller:'message', action:'activity') {}
		"/message/activity/$ownerId/show/$interactionId"(controller:'message', action:'activity') {}

		"/message/folder/$ownerId"(controller:'message', action:'folder') {}
		"/message/folder/$ownerId/show/$interactionId"(controller:'message', action:'folder') {}

		// Don't know why this is neccessary, but it is
		"/poll/create"(controller:'poll', action:'create')
		"/poll/save"(controller:'poll', action:'save')
		"/folder/create"(controller:'folder', action:'create')
		"/folder/save"(controller:'folder', action:'save')
		"/announcement/create"(controller:'announcement', action: 'create')
		"/announcement/save"(controller:'announcement', action: 'save')
		"/autoreply/create"(controller:'autoreply', action: 'create')
		"/autoreply/save"(controller:'autoreply', action: 'save')
		"/autoforward/create"(controller:'autoforward', action: 'create')

		"/customactivity/create"(controller:'customactivity', action: 'create')
		"/customactivity/save"(controller:'customactivity', action: 'save')
		
		"/archive/inbox/show/$interactionId"(controller:'archive', action:'inbox') {}
		"/archive/sent/show/$interactionId"(controller:'archive', action:'sent') {}
		
		"/archive/activity"(controller:'archive', action:'activityList') {}
		"/archive/activity/$ownerId"(controller:'archive', action:'activity') {}
		"/archive/activity/$ownerId/show/$interactionId"(controller:'archive', action:'activity') {}
		
		"/archive/folder"(controller:'archive', action:'folderList') {}
		"/archive/folder/$ownerId"(controller:'archive', action:'folder') {}
		"/archive/folder/$ownerId/show/$interactionId"(controller:'archive', action:'folder') {}

		"/webconnection/$imp/$action"(controller:'webconnection') {}

		"/images/help/$imagePath**.png"(controller:'help', action:'image') {}
		"/help/$helpSection**"(controller:'help', action:'section') {}
		"/help"(controller:'help', action:'index') {}

		"/api/1/$entityClassApiUrl/$entityId/$secret?" controller:'api'

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"500"(view:'/error')
	}
}
