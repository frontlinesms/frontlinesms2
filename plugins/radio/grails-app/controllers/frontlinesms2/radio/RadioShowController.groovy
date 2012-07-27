package frontlinesms2.radio

import frontlinesms2.MessageController
import frontlinesms2.*
import java.util.Date
import grails.converters.*
import java.text.SimpleDateFormat

class RadioShowController extends MessageController {
	static allowedMethods = [save: "POST"]

	def radioShowService
	
	def index() {
		params.sort = 'date'
		redirect(action:messageSection, params:params)
	}
	
	def create() {
		[showInstance: new RadioShow()]
	}

	def save() {
		def showInstance = RadioShow.get(params.ownerId) ?: new RadioShow()
		showInstance.properties = params
		if (showInstance.validate()) {
			showInstance.save()
			flash.message = message(code: 'radio.show.saved')
		} else {
			flash.message = message(code: 'radio.show.invalid.name')
		}
		redirect(controller: 'message', action: "inbox")
}
	
	def radioShow() {
		withRadioShow { showInstance ->
			if(showInstance) {
				def messageInstanceList = showInstance?.getShowMessages(params.starred)
				def radioMessageInstanceList = []
				messageInstanceList?.list(params).inject([]) { messageB, messageA ->
				    if(messageB && dateToString(messageB.date) != dateToString(messageA.date) && params.sort == 'date')
				        radioMessageInstanceList.add(dateToString(messageA.date))
				    radioMessageInstanceList.add(messageA)
				    return messageA
				}
				render view:'standard',
					model:[messageInstanceList: radioMessageInstanceList,
						   messageSection: 'radioShow',
						   messageInstanceTotal: messageInstanceList?.count(), viewingMessages:params.viewingMessages,
						   ownerInstance: showInstance, inArchive:params.inArchive, mainNavSection: showInstance.archived?'archive':'message', inARadioShow: showInstance.archived] << this.getShowModel()
		} else {
			flash.message = message(code: 'radio.show.not.found')
			redirect(action: 'inbox')
		}
		}
	}
	
	def startShow() {
		def showInstance = RadioShow.findById(params.id)
		if(showInstance.archived) {
			flash.message = message code:'radio.show.onair.error.archived'
			render ([ok:false, message:flash.message] as JSON)
		}
		else if(showInstance?.start()) {
			showInstance.save(flush:true)
			render ([ok:true] as JSON)
		} else {
			flash.message = message code:'radio.show.onair.error', args:[RadioShow.findByIsRunning(true)?.name]
			render ([ok:false, message:flash.message] as JSON)
		}
	}
	
	def stopShow() {
		def showInstance = RadioShow.findById(params.id)
		showInstance.stop()
		showInstance.save(flush:true)
		render "$showInstance.id"
	}
	
	private def getShowModel(messageInstanceList) {
		def model = super.getShowModel(messageInstanceList)
		model << [radioShowInstanceList: RadioShow.findAll()]
		return model
	}
	
	def getNewRadioMessageCount = {
		if(params.messageSection == 'radioShow') {
			def messageCount = [totalMessages:[RadioShow.get(params.ownerId)?.getShowMessages()?.count()]]
			render messageCount as JSON
		} else {
			getNewMessageCount()
		}
	}
	
	def addActivity() {
		def activityInstance = Activity.get(params.activityId)
		def showInstance = RadioShow.get(params.radioShowId)
		
		if(showInstance && activityInstance) {
			showInstance.addToActivities(activityInstance)
		}
		else if(activityInstance) {
			RadioShow.findByOwnedActivity(activityInstance).get()?.removeFromActivities(activityInstance)
		}
		redirect controller:activityInstance?.archived?"archive":"message", action:"activity", params: [ownerId: params.activityId]
	}
	
	def selectShow() {
		def activityInstance = Activity.get(params.ownerId)
		def radioShowIntance = RadioShow.findByOwnedActivity(activityInstance).get()
		render template:"selectShow", model:[ownerInstance:activityInstance, radioShowIntance:radioShowIntance, radioShows:RadioShow.findAllByDeleted(false), formtag:true]
	}

	def rename() {
		withRadioShow{ showInstance ->
			[showInstance: showInstance]
		}
	}

	def confirmDelete() {
		def showInstance = RadioShow.get(params.id)
		model:[ownerName:showInstance.name,
				ownerInstance:showInstance]
	}

	def delete() {
		withRadioShow params.id, { showInstance->
			if(showInstance.isRunning){
				flash.message = message code:'radioshow.show.onair.error.delete', args:[RadioShow.findByIsRunning(true)?.name]
				redirect action:"radioShow", params:[ownerId: showInstance.id]
			}else{
				trashService.sendToTrash(showInstance)
				showInstance.activities?.each{ activity ->
					trashService.sendToTrash(activity)
				}
				flash.message = defaultMessage 'trashed'
				redirect controller:"message", action:"inbox"
			}
		}
	}

	def wordCloudStats() {
		def words = ""
		def fmessages = []
		if(params.id){
			def ownerInstance =  MessageOwner.findById(params.id)
			ownerInstance.messages.each{ fmessages << it }
			if(ownerInstance instanceof RadioShow){
				ownerInstance.activities.each{
					it.messages.each{ fmessages << it}
				}
			}
		}else{
			fmessages << Fmessage."${params.messageSection}"()?.list()
		}
		fmessages.text.each{ words+=it+" " }
		words =  words.replaceAll("\\W", " ")//remove all non-alphabet
		def data = words.split()
		if (params.ignoreWords) {
			data -= params.ignoreWords.split(",")
		}
		def freq = [:].withDefault { k -> 0 }
		data.each { freq[it] += 1 }
		freq = freq.sort { a, b -> b.value <=> a.value }
		freq = freq.take(100).sort()
		render freq as JSON
	}

	def archive() {
		withRadioShow params.id, { showInstance ->
			if(showInstance.isRunning) {
				flash.message =  message(code:'radioshow.show.onair.error.archive')
			}
			else if(radioShowService.archive(showInstance as RadioShow)) {
				flash.message = defaultMessage 'archived'
			} else {
				flash.message = defaultMessage 'archive.failed', showInstance.id
			}
			redirect controller:"message", action:"inbox"
		}
	}

	def unarchive() {
		withRadioShow params.id, { showInstance ->
			if(radioShowService.unarchive(showInstance as RadioShow)) {
				flash.message = defaultMessage 'unarchived'
				redirect controller:"radioShow", action:"radioShow", params:[ownerId: showInstance.id]
			} else {
				flash.message = defaultMessage 'unarchive.failed', showInstance.id
				redirect controller:"radioShow", action:"showArchive"
			}
		}
	}	

	def restore() {
		def radioShow = RadioShow.findById(params.id)
		if(radioShow){
			Trash.findByObject(radioShow)?.delete()
			radioShow.deleted = false
			radioShow.messages.each{
				it.isDeleted = false
				it.save(failOnError: true, flush: true)
			}
			radioShow.activities.each{ activity->
				activity.deleted = false
				activity.save()
				activity.messages.each{
					it.isDeleted = false
					it.save(failOnError: true, flush: true)
				}
				Trash.findByObject(activity)?.delete()
			}

			if(radioShow.save()) {
				flash.message = defaultMessage 'restored'
			} else {
				flash.message = defaultMessage 'restore.failed', activity.id
			}
		}
		redirect controller:"message", action:"trash"
	}

	def showArchive() {
		def showInstanceList = RadioShow.findAllByArchivedAndDeleted(true, false)
		render view:'../archive/showArchive', model:[showInstanceList: showInstanceList,
				showInstanceTotal: showInstanceList.size(),
				messageSection: "radioShow", inARadioShow: true, mainNavSection: 'archive']
	}
	
	private String dateToString(Date date) {
		new SimpleDateFormat("EEEE, MMMM dd", Locale.US).format(date)
	}

	private def withRadioShow(id=params.ownerId, Closure c) {
		def showInstance = RadioShow.findByIdAndDeleted(id, false)
		if (showInstance) c showInstance
		else render text:defaultMessage('notfound', params.id)
	}

//TODO clean up default message declaration to prevent future duplication
	private def defaultMessage(String code, Object... args=[]) {
		def messageName = message code:'radio.label'
		return message(code:'default.' + code,
				args:[messageName] + args)
	}
	
}
