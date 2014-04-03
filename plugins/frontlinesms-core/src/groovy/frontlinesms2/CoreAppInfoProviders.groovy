package frontlinesms2


class CoreAppInfoProviders {
	static def statusIndicatorProvider = { app, controller, data ->
		app.mainContext.statusIndicatorService.color
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
				[id:c.id, userMutable:c.isUserMutable(), status:c.status.toString()]
			}
		}

		s.registerProvider('frontlinesync_config_synced_status') { app, controller, data ->
			FrontlinesyncFconnection.findAll().collect { c ->
				[id:c.id, configSynced:c.configSynced, status:c.status.toString(), sendEnabled:c.sendEnabled, receiveEnabled:c.receiveEnabled, missedCallEnabled:c.missedCallEnabled]
			}
		}

		s.registerProvider 'contact_message_stats', contactMessageStats

		s.registerProvider('system_notification') { app, controller, data ->
			SystemNotification.findAllByRd(false).collectEntries { [it.id, it.text] }
		}

		s.registerProvider 'status_indicator', statusIndicatorProvider

		s.registerProvider('inbox_unread') { app, controller, data ->
			TextMessage.countTotalUnreadMessages()
		}

		s.registerProvider('new_messages') { app, controller, data ->
			def section = data.messageSection
			def messageCount
			data.starred = data.starred? Boolean.parseBoolean(data.starred): false
			data.inbound = data.inbound? Boolean.parseBoolean(data.inbound): null
			if(!data.ownerId && section != 'trash') {
				if(section == 'pending') {
					messageCount = TextMessage.countPending(data.failed)
				} else if (section == 'missedCalls') {
					messageCount = MissedCall.inbox(data.starred).count()
				} else {
					messageCount = TextMessage."$section"(data.starred).count()
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
				def message = TextMessage.findByMessageOwnerAndText(c, TextMessage.TEST_MESSAGE_TEXT)
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
			def m = [inbox: TextMessage.countUnreadMessages(), pending: TextMessage.pendingAndNotFailed.count(), activities: [:], folders: [:]]
			Activity.findAllByArchivedAndDeleted(false, false).each { act ->
				m.activities."${act.id}" = TextMessage.countUnreadMessages(act)
			}
			Folder.findAllByArchivedAndDeleted(false, false).each { folder ->
				m.folders."${folder.id}" = TextMessage.countUnreadMessages(folder)
			}
			m.missedCalls = MissedCall.countUnread()
			return m
		}
	}
}

