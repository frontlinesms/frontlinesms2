package frontlinesms2

import frontlinesms2.*
import org.apache.camel.*

class WebconnectionService{

	def preProcess(Exchange x) {
		println "x: ${x}"
		println "x.in: ${x.in}"
		println "x.in.headers: ${x.in.headers}"
		def webConn = Webconnection.get(x.in.headers.'webconnection-id')
		webConn.preProcess(x)
	}

	def postProcess(Exchange x) {
		println "x: ${x}"
		println "x.in: ${x.in}"
		println "x.in.headers: ${x.in.headers}"
		def webConn = Webconnection.get(x.in.headers.'webconnection-id')
		webConn.postProcess(x)
	}

	def handleException(Exchange x) {
		println "Web Connection request failed with exception: ${x.in.body}"
		log.info "Web Connection request failed with exception: ${x.in.body}"
	}

	def send(Fmessage message){
		println "*** sending message ${message}"
		def headers = [:]
		headers.'fmessage-id' = message.id
		headers.'webconnection-id' = message.messageOwner.id
		sendMessageAndHeaders("seda:activity-webconnection-${message.messageOwner.id}", message, headers)
	}
}
