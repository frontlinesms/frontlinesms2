package frontlinesms2

import frontlinesms2.*
import org.apache.camel.*

class WebconnectionService {
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
		println "### WebconnectionService.postProcess() ## headers ## ${x.in.headers}"
		def webConn = Webconnection.get(x.in.headers.'webconnection-id')
		def message = Fmessage.get(x.in.headers.'fmessage-id')
		changeMessageStatus(message, "OWNERDETAIL-COMPLETED")
		webConn.postProcess(x)
	}

	def handleException(Exchange x) {
		def message = Fmessage.get(x.in.headers.'fmessage-id')
		changeMessageStatus(message, "OWNERDETAIL-FAILED")
		println "### WebconnectionService.handleException() ## headers ## ${x.in.headers}"
		println "Web Connection request failed with exception: ${x.in.body}"
		log.info "Web Connection request failed with exception: ${x.in.body}"
	}

	def handleFailed(Exchange x) {
	}

	def handleCompleted(Exchange x) {
	}

	def send(Fmessage message){
		println "## Webconnection.send() ## sending message # ${message}"
		def headers = [:]
		headers.'fmessage-id' = message.id
		headers.'webconnection-id' = message.messageOwner.id
		changeMessageStatus(message, "OWNERDETAIL-PENDING")
		sendMessageAndHeaders("seda:activity-webconnection-${message.messageOwner.id}", message, headers)
	}

	def saveInstance(Webconnection webconnectionInstance, params) {
		webconnectionInstance.keywords?.clear()
		webconnectionInstance.name = params.name
		webconnectionInstance.initialize(params)
		webconnectionInstance.save(flush:true, failOnError:true)
		if (params.sorting == 'disabled') {
			println "##### WebconnectionService.saveInstance() # removing keywords"
		}
		else if(params.sorting == 'global')
			webconnectionInstance.addToKeywords(new Keyword(value:'', isTopLevel:true))
		else if(params.sorting == 'enabled'){
			def keywords = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(',')
			keywords.collect { new Keyword(value:it.trim(), isTopLevel:true) }.each { webconnectionInstance.addToKeywords(it) }
		}
		webconnectionInstance.save(flush:true, failOnError:true)
		return webconnectionInstance
	}

	private changeMessageStatus(Fmessage message, String s){
		message.ownerDetail = s
		message.save()
	}
}
