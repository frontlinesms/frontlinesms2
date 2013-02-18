package frontlinesms2

class CoreAppInfoProviders {
	static void registerAll(AppInfoService s) {
		s.registerProvider('device_detection') { app, controller, data ->
			app.mainContext.deviceDetectionService.detected
		}

		s.registerProvider('connection_show') { app, controller, data ->
			def c = Fconnection.get(data.id)
			if(c) [id:c.id , status:c.status.toString()]
		}

		s.registerProvider('contact_message_stats') { app, controller, data ->
			def c = Contact.get(data.id)
			if(c) {
				[inbound:c.inboundMessagesCount,
						outbound:c.outboundMessagesCount]
			}
		}

		s.registerProvider('system_notification') { app, controller, data ->
			SystemNotification.findAllByRead(false).collectEntries { [it.id, it.text] }
		}

		s.registerProvider('status_indicator') { app, controller, data ->
			def connections = Fconnection.list()
			def color = (connections && connections.status.any {(it == ConnectionStatus.CONNECTED)}) ? 'green' : 'red'
			return color
		}

		s.registerProvider('new_messages') { app, controller, data ->
			def section = data.messageSection
			def messageCount
			if(!data.ownerId && section != 'trash') {
				if(section == 'pending') {
					messageCount = Fmessage.countPending(data.failed)
				} else {
					messageCount = Fmessage."$section"(data.starred).count()
				}
			} else if(section == 'activity') {
				def getSent = null
				if(data.inbound) getSent = Boolean.parseBoolean(data.inbound)
				messageCount = Activity.get(data.ownerId)?.getActivityMessages(data.starred, getSent)?.count()
			} else if(section == 'folder') {
				def getSent = null
				if(data.inbound) getSent = Boolean.parseBoolean(data.inbound)
				messageCount = Folder.get(data.ownerId)?.getFolderMessages(data.starred, getSent)?.count()
			} else messageCount = 0
			return messageCount
		}
	}
}

