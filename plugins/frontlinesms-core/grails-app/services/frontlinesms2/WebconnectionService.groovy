package frontlinesms2

import frontlinesms2.*
import org.apache.camel.*

import grails.converters.JSON

import frontlinesms2.api.*


class WebconnectionService {
	def camelContext
	def i18nUtilService
	def messageSendService

	def retryFailed(Webconnection c) {
		Fmessage.findAllByMessageOwnerAndOwnerDetail(c, Webconnection.OWNERDETAIL_FAILED).each {
			send(it)
		}
	}

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
		println "#### Completed postProcess #### ${x.in.headers.'fmessage-id'}"
		def webConn = Webconnection.get(x.in.headers.'webconnection-id')
		def message = Fmessage.get(x.in.headers.'fmessage-id')
		changeMessageOwnerDetail(message, Webconnection.OWNERDETAIL_SUCCESS)
		webConn.postProcess(x)
	}

	def handleException(Exchange x) {
		def message = Fmessage.get(x.in.headers.'fmessage-id')
		changeMessageOwnerDetail(message, Webconnection.OWNERDETAIL_FAILED)
		println "### WebconnectionService.handleException() ## headers ## ${x.in.headers}"
		println "Web Connection request failed with exception: ${x.in.body}"
		log.info "Web Connection request failed with exception: ${x.in.body}"
	}

	def createStatusNotification(Exchange x) {
		def webConn = Webconnection.get(x.in.headers.'webconnection-id')
		def message = Fmessage.get(x.in.headers.'fmessage-id')
		def text = i18nUtilService.getMessage(code:"webconnection.${message.ownerDetail}.label", args:[webConn.name])
		println "######## StatusNotification::: $text #########"
		def notification = SystemNotification.findByText(text) ?: new SystemNotification(text:text)
		notification.read = false
		notification.save(failOnError:true, flush:true)
	}

	def send(Fmessage message) {
		println "## Webconnection.send() ## sending message # ${message}"
		def headers = [:]
		headers.'fmessage-id' = message.id
		headers.'webconnection-id' = message.messageOwner.id
		changeMessageOwnerDetail(message, Webconnection.OWNERDETAIL_PENDING)
		sendMessageAndHeaders("seda:activity-webconnection-${message.messageOwner.id}", message, headers)
	}

	def saveInstance(Webconnection webconnectionInstance, params) {
		webconnectionInstance.keywords?.clear()
		webconnectionInstance.name = params.name
		webconnectionInstance.initialize(params)
		webconnectionInstance.save(failOnError:true)

		webconnectionInstance.keywords?.clear()
		webconnectionInstance.save(flush:true, failOnError:true)
		if (params.sorting == 'disabled') {
			println "##### WebconnectionService.saveInstance() # removing keywords"
		} else if(params.sorting == 'global') {
			webconnectionInstance.addToKeywords(new Keyword(value:'', isTopLevel:true))
		} else if(params.sorting == 'enabled') {
			def keywords = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(',')
			keywords.collect { new Keyword(value:it.trim(), isTopLevel:true) }.each { webconnectionInstance.addToKeywords(it) }
		}
		webconnectionInstance.save(failOnError:true, flush:true)
	}

	def testRoute(Webconnection webconnectionInstance) {
		def message = Fmessage.findByMessageOwnerAndText(webconnectionInstance, Fmessage.TEST_MESSAGE_TEXT)
		println "testRoute::: $message"
		if(!message) {
			message = createTestMessage()
			webconnectionInstance.addToMessages(message)
			webconnectionInstance.save(failOnError:true)
		}
		webconnectionInstance.createRoute(webconnectionInstance.testRouteDefinitions)
		if(getStatusOf(webconnectionInstance) == ConnectionStatus.CONNECTED) {
			def headers = [:]
			headers.'fmessage-id' = message.id
			headers.'webconnection-id'= webconnectionInstance.id
			sendMessageAndHeaders("seda:activity-webconnection-${webconnectionInstance.id}", message, headers)
			changeMessageOwnerDetail(message, Webconnection.OWNERDETAIL_PENDING)
		} else {
			changeMessageOwnerDetail(message, Webconnection.OWNERDETAIL_FAILED)
		}
	}

	def getStatusOf(Webconnection w) {
		camelContext.routes.any { it.id ==~ /.*activity-webconnection-${w.id}$/ } ? ConnectionStatus.CONNECTED : ConnectionStatus.FAILED
	}

	private changeMessageOwnerDetail(Fmessage message, String s) {
		message.ownerDetail = s
		message.save(failOnError:true, flush:true)
		println "Changing Status ${message.ownerDetail}"
	}

	def apiProcess(webcon, controller) {
		controller.render(generateApiResponse(webcon, controller))
	}

	def generateApiResponse(webcon, controller) {
		def message = controller.request.JSON?.message
		def recipients = controller.request.JSON?.recipients
		def secret = controller.request.JSON?.secret
		def errors = [invalid:[], missing:[]]
		println "JSON IS ${controller.request.JSON}"
		println "MESSAGE IS ${controller.request.JSON?.message}"
		println "RECIPIENTS IS ${controller.request.JSON?.recipients}"

		//> Detect and return 401 (authentication) error conditions
		if(webcon.secret && !secret)
			return [status:401, text:"no secret provided"]
		if(webcon.secret && secret != webcon.secret)
			return [status:401, text:"invalid secret"]

		//> Detect and return 400 (invalid request) error conditions
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
					groups << Group.findByNameIlike(it.name.toLowerCase())
			}
			else if (it.type == "smartgroup") {
				if(it.id != null)
					groups << SmartGroup.get(it.id)
				else if(it.name)
					groups << SmartGroup.findByNameIlike(it.name.toLowerCase())
			}
			else if (it.type == "contact") {
				if(it.id != null)
					addresses << Contact.get(it.id)?.mobile
				else if(it.name)
					addresses << Contact.findByNameIlike(it.name.toLowerCase())?.mobile
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
		if(!m.dispatches)
			return [status:400, text:"no recipients supplied"]
		messageSendService.send(m)
		webcon.addToMessages(m)
		webcon.save(failOnError: true)
		"message successfully queued to send to ${m.dispatches.size()} recipient(s)"
	}

	private Fmessage createTestMessage() {
		Fmessage fm = new Fmessage(src:"0000", text:Fmessage.TEST_MESSAGE_TEXT, inbound:true)
		fm.save(failOnError:true, flush:true)
	}
}
