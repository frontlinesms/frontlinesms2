package frontlinesms2

import frontlinesms2.*
import org.apache.camel.*

import grails.converters.JSON

import frontlinesms2.api.*


class WebconnectionService {
	def messageSendService

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

	def apiProcess(webcon, controller) {
		controller.render(generateApiResponse(webcon, controller))
	}

	def generateApiResponse(webcon, controller) {
		def message = controller.params['JSON'].message
		def recipients = controller.params['JSON'].recipients
		def errors = [invalid:[], missing:[]]
		println "JSON IS ${controller.params['JSON']}"
		println "MESSAGE IS ${controller.params['JSON'].message}"
		println "RECIPIENTS IS ${controller.params['JSON'].recipients}"

		//> Detect and return error conditions
		if (!message)
			errors.missing << "message"
		if (recipients == null)
			errors.missing << "recipients"
		if (recipients == [])
			errors.invalid << "no recipients supplied"
		if (errors.invalid || errors.missing) {
			def errorList = []
			if (errors.invalid) errorList << (errors.invalid.join(", "))
			if (errors.missing) errorList << "missing required field(s): " + errors.missing.join(", ")
			def errorMessage = errorList.join(", ")
			println "errorMessage ::: $errorMessage"
			return [status:400, text:errorMessage]
		}

		//> Populate destinations
		println "evaluating the destinations for $recipients ...."
		def groups = []
		def addresses = []
		recipients.each {
			if(it.type == "group") {
				if(it.id != null)
					groups << Group.get(it.id)
				else if(it.name)
					groups << Group.findByName(it.name)
			}
			else if (it.type == "smartgroup") {
				if(it.id != null)
					groups << SmartGroup.get(it.id)
				else if(it.name)
					groups << SmartGroup.findByName(it.name)
			}
			else if (it.type == "contact") {
				if(it.id != null)
					addresses << Contact.get(it.id).mobile
				else if(it.name)
					addresses << Contact.findByName(it.name).mobile
			}
			else if (it.type == "address") {
				addresses << it.value
			}
		}
		groups = groups.unique()
		addresses = addresses.unique()
		println "groups: $groups. Addresses: $addresses"

		//> Send message
		def m = messageSendService.createOutgoingMessage([messageText: message, addresses: addresses, groups: groups])
		println "I am about to send $m"
		messageSendService.send(m)
		webcon.addToMessages(m)
		webcon.save(failOnError: true)
	}
}

