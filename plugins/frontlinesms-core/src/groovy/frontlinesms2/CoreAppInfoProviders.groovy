package frontlinesms2

import frontlinesms2.ConnectionStatus as CS

class CoreAppInfoProviders {
	static def statusIndicatorProvider = { app, controller, data ->
		def status = Fconnection.list()*.status
		if(CS.FAILED in status) {
			return 'red'
		} else if(CS.CONNECTING in status) {
			return 'orange'
		} else if(CS.CONNECTED in status) {
			return 'green'
		}
		return 'grey'
	}

	static def contactMessageStats =  { app, controller, data ->
		def c = Contact.get(data.id)
		if(c) {
			[inbound:c.inboundMessagesCount,
					outbound:c.outboundMessagesCount]
		}
	}

	static void registerAll(AppInfoService s) {
		s.registerProvider('device_detection') { app, controller, data ->
			app.mainContext.deviceDetectionService.detected
		}

		s.registerProvider('fconnection_statuses') { app, controller, data ->
			Fconnection.findAll().collect { c ->
				[id:c.id, status:c.status.toString()]
			}
		}

		s.registerProvider 'contact_message_stats', contactMessageStats

		s.registerProvider('system_notification') { app, controller, data ->
			SystemNotification.findAllByRead(false).collectEntries { [it.id, it.text] }
		}

		s.registerProvider 'status_indicator', statusIndicatorProvider

		s.registerProvider('inbox_unread') { app, controller, data ->
			Fmessage.countTotalUnreadMessages()
		}

		s.registerProvider('new_messages') { app, controller, data ->
			def section = data.messageSection
			def messageCount
			data.starred = data.starred? Boolean.parseBoolean(data.starred): false
			data.inbound = data.inbound? Boolean.parseBoolean(data.inbound): null
			if(!data.ownerId && section != 'trash') {
				if(section == 'pending') {
					messageCount = Fmessage.countPending(data.failed)
				} else {
					messageCount = Fmessage."$section"(data.starred).count()
				}
			} else if(section == 'activity') {
				messageCount = Activity.get(data.ownerId)?.getMessageCount(data.starred, data.inbound)
			} else if(section == 'folder') {
				messageCount = Folder.get(data.ownerId)?.getFolderMessages(data.starred, data.inbound)?.count()
			} else messageCount = 0
			return messageCount
		}

		s.registerProvider('webconnection_status') { app, controller, data ->
			def c = Webconnection.get(data.ownerId)
			def response = [ownerId:data.ownerId, ok:true]
			if(c) {
				def message = Fmessage.findByMessageOwnerAndText(c, Fmessage.TEST_MESSAGE_TEXT)
				response.status = message?.ownerDetail
			} else {
				response.ok = false
			}
			return response
		}

		s.registerProvider('poll_stats') { app, controller, data ->
			Poll.get(data.ownerId)?.responseStats
		}

		s.registerProvider('new_message_summary') { app, controller, data ->
			def m = [inbox: Fmessage.countUnreadMessages(), pending: Fmessage.pendingAndNotFailed.count(), activities: [:], folders: [:]]
			Activity.findAllByArchivedAndDeleted(false, false).each { act ->
				m.activities."${act.id}" = Fmessage.countUnreadMessages(act)
			}
			Folder.findAllByArchivedAndDeleted(false, false).each { folder ->
				m.folders."${folder.id}" = Fmessage.countUnreadMessages(folder)
			}
			return m
		}
	}
}

