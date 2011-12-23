package frontlinesms2.radio

import frontlinesms2.Fmessage

class RadioShowService {

	private RadioShow currentShow

	def startShow(RadioShow radioShow) {
		if(!isRunning()) {
			currentShow = radioShow
		}
    }
	
	def stopShow() {
		if(isRunning()) {
			currentShow = null
		}
	}
	
	def isRunning() {
		currentShow
	}
	
	def process(Fmessage message) {
		if(isRunning()) {
			currentShow.addToMessages(message)
			currentShow.merge(flush:true)
		}
	}
}