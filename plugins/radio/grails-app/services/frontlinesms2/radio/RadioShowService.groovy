package frontlinesms2.radio

import frontlinesms2.Fmessage

class RadioShowService {

	def process(Fmessage message) {
		def runningShow = RadioShow.findByIsRunning(true)
		if(runningShow && message.messageOwner == null) {
			runningShow.addToMessages(message)
			runningShow.save(failOnError:true)
		}
	}
}