package frontlinesms2

import grails.util.GrailsConfig
import grails.converters.*
import java.lang.*

class MessageController {
	
	static allowedMethods = [save: "POST", update: "POST",
			delete: "POST", deleteAll: "POST",
			archive: "POST", archiveAll: "POST"]

	def messageSendService
	def fmessageInfoService
	def trashService

	def bobInterceptor = {
		params.sort = params.sort ?: 'date'
		params.order = params.order ?: 'desc'
		params.viewingArchive = params.viewingArchive ? params.viewingArchive.toBoolean() : false
		params.starred = params.starred ? params.starred.toBoolean() : false
		params.failed = params.failed ? params.failed.toBoolean() : false
		params.max = params.max ?: GrailsConfig.config.grails.views.pagination.max
		params.offset  = params.offset ?: 0
	}
	def beforeInterceptor = bobInterceptor
	
	def index = {
		params.sort = 'date'
		redirect(action:'inbox', params:params)
	}
	
	def getNewMessageCount = {
		def section = params.messageSection
		if(!params.ownerId && section != 'trash') {
			def messageCount = [totalMessages:[Fmessage."$section"().count()]]
			render messageCount as JSON
		} else if(section == 'activity') {
			def messageCount = [totalMessages:[Activity.get(params.ownerId)?.getActivityMessages()?.count()]]
			render messageCount as JSON
		} else if(section == 'folder') {
			def messageCount = [totalMessages:[Folder.get(params.ownerId)?.getFolderMessages()?.count()]]
			render messageCount as JSON
		} else
			render ""
	}
	
	def getShowModel(messageInstanceList) {
		def messageInstance = (params.messageId) ? Fmessage.get(params.messageId) : messageInstanceList ? messageInstanceList[0]:null
		if (messageInstance && !messageInstance.read) {
			messageInstance.read = true
			messageInstance.save()
		}
		def checkedMessageCount = params.checkedMessageList?.tokenize(',')?.size()
		def selectedMessageList = params.checkedMessageList?: ',' + messageInstance?.id + ','
		[messageInstance: messageInstance,
				checkedMessageCount: checkedMessageCount,
				checkedMessageList: selectedMessageList,
				activityInstanceList: Activity.findAllByArchivedAndDeleted(params.viewingArchive, false),
				folderInstanceList: Folder.findAllByArchivedAndDeleted(params.viewingArchive, false),
				messageCount: Fmessage.countAllMessages(params),
				hasFailedMessages: Fmessage.hasFailedMessages(),
				failedDispatchCount: (messageInstance && messageInstance.hasFailed) ? Dispatch.findAllByMessageAndStatus(messageInstance, DispatchStatus.FAILED).size() : 0,
				viewingArchvive: params.viewingArchive]
	}

	def inbox = {
		def messageInstanceList = Fmessage.inbox(params.starred, params.viewingArchive)
		render view:'standard',
					model:[messageInstanceList: messageInstanceList.list(params),
							messageSection: 'inbox',
							messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	}

	def sent = {
		def messageInstanceList = Fmessage.sent(params.starred, params.viewingArchive)
		render view:'standard', model:[messageSection: 'sent',
				messageInstanceList: messageInstanceList.list(params),
				messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	}

	def pending = {
		def messageInstanceList = Fmessage.pending(params.failed)
		render view:'standard', model:[messageInstanceList: messageInstanceList.list(params),
				messageSection: 'pending',
				messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	}
	
	def trash = {
		def trashInstance
		def trashInstanceList
		def messageInstanceList
		params.sort = (params.sort && params.sort != 'date') ?: "dateCreated"
		if(params.id) {
			def setTrashInstance = { obj ->
				if(obj.objectType == "frontlinesms2.Fmessage") {
					params.messageId = obj.linkId
				} else {
					trashInstance = obj.link
				}
			}
			setTrashInstance(Trash.findById(params.id))
		}
		
		if(params.starred) {
			messageInstanceList = Fmessage.deleted(params.starred)
		} else {
			trashInstanceList =  Trash.list(params)
		}
		render view:'standard', model:[trashInstanceList:trashInstanceList,
					messageInstanceList: messageInstanceList?.list(params),
					messageSection: 'trash',
					messageInstanceTotal: Trash.count(),
					ownerInstance: trashInstance] << getShowModel()
	}

	def poll = { redirect(action: 'activity', params: params) }
	def announcement = { redirect(action: 'activity', params: params) }
	def activity = {
		def activityInstance = Activity.get(params.ownerId)
		def messageInstanceList = activityInstance?.getActivityMessages(params.starred)
		def sentMessageCount = 0
		Fmessage.findAllByMessageOwnerAndInbound(activityInstance, false).each {
			sentMessageCount += it.dispatches.size()
		}
		render view:"../message/${activityInstance.type == 'poll' ? 'poll' : 'standard'}",
			model:[messageInstanceList: messageInstanceList?.list(params),
					messageSection: 'activity',
					messageInstanceTotal: messageInstanceList?.count(),
					ownerInstance: activityInstance,
					viewingMessages: params.viewingArchive ? params.viewingMessages : null,
					pollResponse: activityInstance?.type == 'poll' ? activityInstance.responseStats as JSON : null,
					sentMessageCount: sentMessageCount] << getShowModel()
	}
	
	def folder = {
		def folderInstance = Folder.get(params.ownerId)
		def messageInstanceList = folderInstance?.getFolderMessages(params.starred)
		if(params.flashMessage) { flash.message = params.flashMessage }
		render view:'../message/standard', model:[messageInstanceList: messageInstanceList.list(params),
					messageSection: 'folder',
					messageInstanceTotal: messageInstanceList.count(),
					ownerInstance: folderInstance,
					viewingMessages: params.viewingArchive ? params.viewingMessages : null] << getShowModel()
	}

	def send = {
		def message = messageSendService.getMessagesToSend(params)
		messageSendService.send(message)
		flash.message = "Message has been queued to send to " + message.dispatches*.dst.join(", ")
		render(text: flash.message)
	}
	
	def retry = {
		def failedMessageIds = params.failedMessageIds ?: params.messageId
		def messages = Fmessage.getAll([failedMessageIds].flatten())
		if(messages) {
			messages.each { message ->
				messageSendService.send(message)
			}
			flash.message = "Message has been queued to send to " + messages*.dispatches*.dst.flatten().join(", ")
		}
		redirect (controller: "message", action: 'pending')
	}

	def delete = {
		def messageIdList = params.checkedMessageList ? params.checkedMessageList.tokenize(',') : [params.messageId]
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				messageInstance.isDeleted = true
				new Trash(identifier:messageInstance.displayName, message:messageInstance.text, objectType:messageInstance.class.name, linkId:messageInstance.id).save(failOnError: true, flush: true)
				messageInstance.save(failOnError: true, flush: true)
			}
		}
		flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: ''), messageIdList.size() + ' message(s)'])}"
		if(params.messageSection == 'result')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
		else
			redirect(action: params.messageSection, params: [ownerId: params.ownerId, viewingArchive: params.viewingArchive, starred: params.starred, failed: params.failed])
	}
	
	def archive = {
		def messageIdList = params.checkedMessageList ? params.checkedMessageList.tokenize(',') : [params.messageId]
		def listSize = messageIdList.size();
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				if(!messageInstance.messageOwner) {
					messageInstance.archived = true
					messageInstance.save(failOnError: true, flush: true)
				} else {
					listSize--
				}
			}
		}
		flash.message = "${message(code: 'default.archived.message', args: [message(code: 'message.label', default: ''), listSize + ' message(s)'])}"
		if(params.messageSection == 'result')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, messageId: params.messageId])
		else
			redirect(controller: 'message', action: params.messageSection, params: [ownerId: params.ownerId])
	}
	
	def unarchive = {
		def messageIdList = params.checkedMessageList ? params.checkedMessageList.tokenize(',') : [params.messageId]
		def listSize = messageIdList.size();
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				if(!messageInstance.messageOwner) {
					messageInstance.archived = false
					messageInstance.save(failOnError: true, flush: true)
				} else {
					listSize--
				}
			}
		}
		flash.message = "${message(code: 'default.unarchived.message', args: [message(code: 'message.label', default: ''), listSize + ' message(s)'])}"
		if(params.messageSection == 'result')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, messageId: params.messageId])
		else
			redirect(controller: 'archive', action: params.messageSection, params: [ownerId: params.ownerId])
	}

	def move = {
		def messageIdList = params.messageId.tokenize(',')
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				if (messageInstance.isDeleted == true)
					messageInstance.isDeleted = false
				if(Trash.findByLinkId(messageInstance.id))
					Trash.findByLinkId(messageInstance.id).delete(flush:true)
				if (params.messageSection == 'activity') {
					def activity = Activity.get(params.ownerId)
					activity.addToMessages(messageInstance)
					activity.save(failOnError: true, flush: true)				
				} else if (params.messageSection == 'folder' || params.messageSection == 'radioShow') {
					MessageOwner.get(params.ownerId).addToMessages(messageInstance).save()
				} else {
					messageInstance.with {
						messageOwner?.removeFromMessages messageInstance
						messageOwner = null
						messageOwner?.save()
						save()
					}
				}
			}
		}
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: ''), messageIdList.size() + ' message(s)'])}"
		render ""
	}

	def changeResponse = {
		def messageIdList = params.messageId.tokenize(',')
		messageIdList.each { id ->
			withFmessage id, { messageInstance ->
				def responseInstance = PollResponse.get(params.responseId)
				responseInstance.addToMessages(messageInstance)
				responseInstance.save(failOnError: true, flush: true)
			}
		}
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: 'Fmessage'), 'message(s)'])}"
		render ""
	}

	def changeStarStatus = {
		withFmessage { messageInstance ->
			messageInstance.starred =! messageInstance.starred
			messageInstance.save(failOnError: true, flush: true)
			Fmessage.get(params.messageId).messageOwner?.refresh()
            params.remove('messageId')
			render(text: messageInstance.starred ? "starred" : "unstarred")
		}
	}
	
	def showRecipients = {
		def groupList = []
		def contactList = []
		def addressList = []
		def finalAddressList = []
		
		def message = Fmessage.get(params.messageId) ?: null
		if(message) {
			addressList = message.dispatches
			Group.getAll().each {
				def groupAddressList = it.getAddresses()
				if (groupAddressList != [] && addressList.dst.containsAll(groupAddressList)) {
					groupList += it
				}
			}
			message.dispatches.each {
				if(Contact.findByPrimaryMobile(it.dst) || Contact.findBySecondaryMobile(it.dst)) {
					contactList += Contact.findByPrimaryMobile(it.dst) ? "${Contact.findByPrimaryMobile(it.dst).name} (${it.status})" : null
					contactList += Contact.findBySecondaryMobile(it.dst) ? "${Contact.findBySecondaryMobile(it.dst).name} (${it.status})" : null
					addressList -= it
				}
			}
		}
		addressList.each {
			finalAddressList += "${it.dst} (${it.status})"
		}
		contactList = contactList - null
		[groupList: groupList,
			contactList: contactList,
			addressList: finalAddressList]
	}

	def confirmEmptyTrash = { }
	
	def emptyTrash = {
		trashService.emptyTrash()
		redirect(action: 'inbox')
	}
	
	def getUnreadMessageCount = {
		render text: Fmessage.countUnreadMessages(), contentType:'text/plain'
	}
	
	def getSendMessageCount = {	
		def messageInfo
		def message = params.message ?: ''
		if(message)	{ 
			messageInfo = fmessageInfoService.getMessageInfos(message)
			def messageCount = messageInfo.partCount > 1 ? "${messageInfo.partCount} SMS messages": "1 SMS message"
			render text: "Characters remaining ${messageInfo.remaining} ($messageCount)", contentType:'text/plain'
		} else {
			render text: "Characters remaining 160 (1 SMS message)", contentType:'text/plain'
		}
		
	}
	
	private def withFmessage(messageId = params.messageId, Closure c) {
			def m = Fmessage.get(messageId.toLong())
			if(m) c.call(m)
			else render(text: "Could not find message with id ${params.messageId}") // TODO handle error state properly
	}
}
