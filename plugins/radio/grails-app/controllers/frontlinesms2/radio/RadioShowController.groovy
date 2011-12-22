package frontlinesms2.radio

import frontlinesms2.MessageController
import java.util.Date
import java.text.SimpleDateFormat

class RadioShowController extends MessageController {
	static allowedMethods = [save: "POST"]
//	static layout = 'radioShows'
	def index = {
		redirect action:radioShow
	}
	def create = {}

	def save = {	
		def showInstance = new RadioShow()
		showInstance.properties = params
		if (showInstance.validate()) {
			showInstance.save()
			flash.message = "Radio show created"
		}
		else {
			flash.message = "Name is not valid"
		}
		redirect(controller: 'message', action: "inbox")
	}
	
	def radioShow = {
		def showInstance = RadioShow.get(params.ownerId)
		def messageInstanceList = showInstance?.getShowMessages(params.starred)
		
		def radioMessageInstanceList = []
		messageInstanceList?.list(params).inject([]) { messageB, messageA ->
		    if(messageB && dateToString(messageB.dateReceived) != dateToString(messageA.dateReceived))
		        radioMessageInstanceList.add(dateToString(messageA.dateReceived))
		    radioMessageInstanceList.add(messageA)
		    return messageA
		}
		render view:'standard', model:[messageInstanceList: radioMessageInstanceList,
			   messageSection: 'radioShow',
			   messageInstanceTotal: messageInstanceList?.count(),
			   ownerInstance: showInstance] << this.getShowModel()
	}
	
	def getShowModel(messageInstanceList) {
		def model = super.getShowModel(messageInstanceList)
		model << [radioShows: RadioShow.findAll()]
		return model
	}
	
	String dateToString(Date date) {
		new SimpleDateFormat("EEEE, MMMM dd", Locale.US).format(date)
	}
	
}