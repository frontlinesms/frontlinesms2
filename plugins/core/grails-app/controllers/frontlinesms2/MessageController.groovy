package frontlinesms2

import grails.converters.*

class MessageController {
//> CONSTANTS
	static allowedMethods = [save: "POST", update: "POST", delete: "POST", archive: "POST"]

//> SERVICES
	def messageSendService
	def fmessageInfoService
	def trashService

//> INTERCEPTORS
	def bobInterceptor = {
		params.sort = params.sort ?: 'date'
		params.order = params.order ?: 'desc'
		params.starred = params.starred ? params.starred.toBoolean() : false
		params.failed = params.failed ? params.failed.toBoolean() : false
		params.max = params.max?: grailsApplication.config.grails.views.pagination.max
		params.offset  = params.offset ?: 0
		return true
	}
	def beforeInterceptor = bobInterceptor
	
//> ACTIONS
	def index = {
		params.sort = 'date'
		redirect(action:'inbox', params:params)
	}
	
	def newMessageCount = {
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

	def show = {
		def messageInstance = Fmessage.get(params.id)
		messageInstance.read = true
		messageInstance.save()
		def model = [messageInstance: messageInstance,
				folderInstanceList: Folder.findAllByArchivedAndDeleted(viewingArchive, false),
				activityInstanceList: Activity.findAllByArchivedAndDeleted(viewingArchive, false),
				messageSection: params.messageSection]
		render view:'/message/_single_message_details', model:model
	}

	def inbox = {
		def messageInstanceList = Fmessage.inbox(params.starred, this.viewingArchive)
		render view:'../message/standard',
				model:[messageInstanceList: messageInstanceList.list(params),
						messageSection: 'inbox',
						messageInstanceTotal: messageInstanceList.count()] << getShowModel()
	}

	def sent = {
		def messageInstanceList = Fmessage.sent(params.starred, this.viewingArchive)
		render view:'../message/standard', model:[messageSection: 'sent',
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
		def trashedObject
		def trashInstanceList
		def messageInstanceList
		params.sort = (params.sort && params.sort != 'date') ?: "dateCreated"
		if(params.id) {
			def setTrashInstance = { obj ->
				if(obj.objectClass == frontlinesms2.Fmessage) {
					params.messageId = obj.objectId
				} else {
					trashedObject = obj.object
				}
			}
			setTrashInstance(Trash.findById(params.id))
		}
		if(params.starred) {
			messageInstanceList = Fmessage.deleted(params.starred)
		} else {
			trashInstanceList = Trash.list(params)
		}
		render view:'standard', model:[trashInstanceList: trashInstanceList,
					messageInstanceList: messageInstanceList?.list(params),
					messageSection: 'trash',
					messageInstanceTotal: Trash.count(),
					ownerInstance: trashedObject] << getShowModel()
	}

	def poll = { redirect(action: 'activity', params: params) }
	def announcement = { redirect(action: 'activity', params: params) }
	def autoreply = { redirect(action: 'activity', params: params) }
	def activity = {
		def activityInstance = Activity.get(params.ownerId)
		if (activityInstance) {
			def messageInstanceList = activityInstance.getActivityMessages(params.starred, true)
			def sentMessageCount = 0
			def sentDispatchCount = 0
			Fmessage.findAllByMessageOwnerAndInbound(activityInstance, false).each {
				sentDispatchCount += it.dispatches.size()
				sentMessageCount++
			}
			render view:"../message/${activityInstance instanceof Poll ? 'poll' : 'standard'}",
				model:[messageInstanceList: messageInstanceList?.list(params),
						messageSection: 'activity',
						messageInstanceTotal: messageInstanceList?.count(),
						ownerInstance: activityInstance,
						viewingMessages: this.viewingArchive ? params.viewingMessages : null,
						pollResponse: activityInstance instanceof Poll ? activityInstance.responseStats as JSON : null,
						sentMessageCount: sentMessageCount,
						sentDispatchCount: sentDispatchCount] << getShowModel()
		} else {
			flash.message = message(code: 'flash.message.activity.found.not')
			redirect(action: 'inbox')
		}
	}
	
	def folder = {
		def folderInstance = Folder.get(params.ownerId)
		if (folderInstance) {
			def messageInstanceList = folderInstance?.getFolderMessages(params.starred)
			if (params.flashMessage) { flash.message = params.flashMessage }
			render view:'../message/standard', model:[messageInstanceList: messageInstanceList.list(params),
						messageSection: 'folder',
						messageInstanceTotal: messageInstanceList.count(),
						ownerInstance: folderInstance,
						viewingMessages: this.viewingArchive ? params.viewingMessages : null] << getShowModel()
		} else {
			flash.message = message(code: 'flash.message.folder.found.not')
			redirect(action: 'inbox')
		}
	}

	def send = {
		def fmessage = messageSendService.createOutgoingMessage(params)
		messageSendService.send(fmessage)
		flash.message = message(code: 'flash.message.fmessage.in.queue', args: [fmessage.dispatches*.dst?.join(", ")])
		render(text: flash.message)
	}
	
	def retry = {
		def dst = []
		def failedMessageIdList = getCheckedMessageList()
		failedMessageIdList.each { id ->
			withFmessage id, {messageInstance ->
				messageInstance.dispatches.each { 
					if(it.status == DispatchStatus.FAILED) { 
						dst << Contact.findByMobile(it.dst)?.name ?: it.dst
					}
				}
				messageSendService.retry(messageInstance)
			}
		}
		
		flash.message = message(code: 'flash.message.fmessage.in.queue', args: [dst.flatten().join(", ")])
		redirect (controller: "message", action: 'pending')
	}
	
	def delete = {
		def messageIdList = getCheckedMessageList()
		messageIdList.each { id ->
			withFmessage id, { messageInstance ->
				TrashService.sendToTrash(messageInstance)
			}
		}
		flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'message.label', default: ''), messageIdList.size() + message(code: 'flash.message.fmessage')])}"
		if (params.messageSection == 'result')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId])
		else
			redirect(controller: params.controller, action: params.messageSection, params: [ownerId: params.ownerId, starred: params.starred, failed: params.failed])
	}
	
	def archive = {
		def messageIdList = getCheckedMessageList()
		def listSize = messageIdList.size()
		messageIdList.each { id ->
			withFmessage id, { messageInstance ->
				if(!messageInstance.messageOwner) {
					messageInstance.archived = true
					messageInstance.save()
				} else {
					listSize--
				}
			}
		}
		flash.message = "${message(code: 'default.archived.message', args: [message(code: 'message.label', default: ''), listSize + message(code: 'flash.message.fmessage')])}"
		if(params.messageSection == 'result') {
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, messageId: params.messageId])
		} else {
			redirect(controller: 'message', action: params.messageSection, params: [ownerId: params.ownerId])
		}
	}
	
	def unarchive = {
		def messageIdList = getCheckedMessageList()
		def listSize = messageIdList.size()
		messageIdList.each { id ->
			withFmessage id, {messageInstance ->
				if(!messageInstance.messageOwner) {
					messageInstance.archived = false
					messageInstance.save(failOnError: true)
				} else {
					listSize--
				}
			}
		}
		flash.message = "${message(code: 'default.unarchived.message', args: [message(code: 'message.label', default: ''), listSize + message(code: 'flash.message.fmessage')])}"
		if(params.messageSection == 'result')
			redirect(controller: 'search', action: 'result', params: [searchId: params.searchId, messageId: params.messageId])
		else
			redirect(controller: 'archive', action: params.messageSection, params: [ownerId: params.ownerId])
	}

	def move = {
		def messageIdList = params.messageId.tokenize(',')
		messageIdList.each { id ->
			withFmessage id, { messageInstance ->
				messageInstance.isDeleted = false
				Trash.findByLinkId(messageInstance.id)?.delete(failOnError:true)
				if (params.messageSection == 'activity') {
					def activity = Activity.get(params.ownerId)
					activity.addToMessages(messageInstance)
					activity.save(failOnError:true)
					/* FIXME the following is broken for multiple messages, and it's not clear what
					 * it's trying to do.  If this is meant to be triggering an action on the Activity
					 * then it definitely shouldn't be doing it via another controller
					if(activity && activity.autoreplyText)
						redirect(controller: activity instanceof frontlinesms2.Poll ? 'poll' : 'autoreply',
								action: 'sendReply', params: [ownerId: activity.id, messageId: messageInstance.id]) */
				} else if (params.messageSection == 'folder' || params.messageSection == 'radioShow') {
					MessageOwner.get(params.ownerId).addToMessages(messageInstance).save()
				} else {
					messageInstance.with {
						messageOwner?.removeFromMessages(messageInstance)
						messageOwner = null
						messageOwner?.save()
						save()
					}
				}
			}
		}

		// FIXME this flash message is concatenated in a stupid way
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: ''), messageIdList.size() + message(code: 'flash.message.fmessage')])}"
		render ""
	}

	def changeResponse = {
		def messageIdList = params.messageIdList?.tokenize(',') ?: [params.messageId]
		def responseInstance = PollResponse.get(params.responseId)
		messageIdList.each { id ->
			withFmessage id, { messageInstance ->
				responseInstance.poll.removeFromMessages(messageInstance)
				responseInstance.addToMessages(messageInstance)
				responseInstance.poll.save()
			}
		}
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'message.label', default: 'Fmessage'), message(code: 'flash.message.fmessage')])}"
		render ""
	}

	def changeStarStatus = {
		withFmessage { messageInstance ->
			messageInstance.starred =! messageInstance.starred
			messageInstance.save(failOnError: true)
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
				Contact c = Contact.findByMobile(it.dst)
				if(c) {
					contactList += "${c.name} (${it.status})"
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
		def fmessage = params.message ?: ''
		if(fmessage)	{ 
			messageInfo = fmessageInfoService.getMessageInfos(fmessage)
			def messageCount = messageInfo.partCount > 1 ? message(code: 'flash.message.fmessages.many', args: [messageInfo.partCount]): message(code: 'flash.message.fmessages.many.one')
			render text: message(code: 'fmessage.remaining.characters.text', args: [messageInfo.remaining, messageCount]), contentType:'text/plain'
		} else {
			render text: message(code: 'fmessage.remaining.characters.text.all'), contentType:'text/plain'
		}
		
	}

//> PRIVATE HELPERS
	boolean isViewingArchive() { params.controller=='archive' }

	private def withFmessage(messageId = params.messageId, Closure c) {
			def m = Fmessage.get(messageId.toLong())
			if(m) c.call(m)
			else render(text: message(code: 'fmessage.exist.not', args: [params.messageId])) // TODO handle error state properly
	}
	}
	private def getShowModel(messageInstanceList) {
		def messageInstance = params.messageId? Fmessage.get(params.messageId):
				messageInstanceList? messageInstanceList[0]: null
		def checkedMessageCount = getCheckedMessageList().size()
		[messageInstance: messageInstance,
				checkedMessageCount: checkedMessageCount,
				activityInstanceList: Activity.findAllByArchivedAndDeleted(viewingArchive, false),
				folderInstanceList: Folder.findAllByArchivedAndDeleted(viewingArchive, false),
				messageCount: Fmessage.countAllMessages(params),
				hasFailedMessages: Fmessage.hasFailedMessages(),
				failedDispatchCount: messageInstance?.hasFailed ? Dispatch.findAllByMessageAndStatus(messageInstance, DispatchStatus.FAILED).size() : 0]
	}

	private def getCheckedMessageList() {
		def checked = params['message-select']?: [params.messageId]
		if(checked instanceof String) checked = [checked]
		return checked
	}
}

