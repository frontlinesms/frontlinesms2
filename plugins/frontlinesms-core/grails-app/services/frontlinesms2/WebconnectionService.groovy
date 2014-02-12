package frontlinesms2

import org.apache.camel.*

import grails.converters.JSON

import frontlinesms2.api.*

class WebconnectionService {
	private static final REPLACEMENT_KEY = /[$][{]*[a-z_]*[}]/

	def camelContext
	def i18nUtilService
	def messageSendService

	private String getReplacement(String arg, TextMessage msg) {
		arg = (arg - '${') - '}'
		def c = Webconnection.subFields[arg]
		return c ? c(msg) : arg
	}

	private changeMessageOwnerDetail(activityOrStep, message, s) {
		log.info "Status to set to $message is $s"
		message.setMessageDetail(activityOrStep, s)
		message.save(failOnError:true, flush:true)
		log.info "Changing Status ${message.ownerDetail}"
	}

	private def createRoute(webconnectionInstance, routes) {
		try {
			deactivate(webconnectionInstance)
			camelContext.addRouteDefinitions(routes)
			LogEntry.log("Created Webconnection routes: ${routes*.id}")
		} catch(FailedToCreateProducerException ex) {
			log.info ex
		} catch(Exception ex) {
			log.info ex
			deactivate(webconnectionInstance)
		}
	}

	private TextMessage createTestMessage() {
		TextMessage fm = new TextMessage(src:"0000", text:TextMessage.TEST_MESSAGE_TEXT, inbound:true)
		fm.save(failOnError:true, flush:true)
	}

	private def getWebConnection(Exchange x) {
		def webConnection
		if(x.in.headers.'webconnection-id') {
			webConnection = Webconnection.get(x.in.headers.'webconnection-id')
		} else if(x.in.headers.'webconnectionStep-id') {
			webConnection = WebconnectionActionStep.get(x.in.headers.'webconnectionStep-id')
		}
		webConnection
	}


	String getProcessedValue(prop, msg) {
		def val = prop.value
		def matches = val.findAll(REPLACEMENT_KEY)
		matches.each { match ->
			val = val.replaceFirst(REPLACEMENT_KEY, getReplacement(match, msg))
		}
		return val
	}
	
	void preProcess(Exchange x) {
		log.info "x: ${x}"
		log.info "x.in: ${x.in}"
		log.info "x.in.headers: ${x.in.headers}"
		def webConn = getWebConnection(x)
		webConn.preProcess(x)
	}

	void postProcess(Exchange x) {
		log.info "x: ${x}"
		log.info "x.in: ${x.in}"
		log.info "x.in.headers: ${x.in.headers}"
		log.info "### WebconnectionService.postProcess() ## headers ## ${x.in.headers}"
		log.info "#### Completed postProcess #### ${x.in.headers.'fmessage-id'}"
		def webConn = getWebConnection(x)
		def message = TextMessage.get(x.in.headers.'fmessage-id')
		changeMessageOwnerDetail(webConn, message, Webconnection.OWNERDETAIL_SUCCESS)
		webConn.postProcess(x)
	}

	def handleException(Exchange x) {
		def message = TextMessage.get(x.in.headers.'fmessage-id')
		changeMessageOwnerDetail(getWebConnection(x), message, Webconnection.OWNERDETAIL_FAILED)
		log.info "### WebconnectionService.handleException() ## headers ## ${x.in.headers}"
		log.info "Web Connection request failed with exception: ${x.in.body}"
		log.info "Web Connection request failed with exception: ${x.in.body}"
	}

	def createStatusNotification(Exchange x) {
		def webConn
		if(x.in.headers.'webconnection-id') {
			webConn = Webconnection.get(x.in.headers.'webconnection-id')
		} else if(x.in.headers.'webconnectionStep-id') {
			webConn = WebconnectionActionStep.get(x.in.headers.'webconnectionStep-id')
		}
		def message = TextMessage.get(x.in.headers.'fmessage-id')
		def text = i18nUtilService.getMessage(code:"webconnection.${message.ownerDetail}.label", args:[webConn.name])
		log.info "######## StatusNotification::: $text #########"
		def notification = SystemNotification.findOrCreateByText(text)
		notification.read = false
		notification.save(failOnError:true, flush:true)
	}

	def doUpload(activityOrStep, message) {
		log.info "## Webconnection.doUpload() ## uploading message # ${message}"
		def headers = [:]
		headers.'fmessage-id' = message.id
		if(activityOrStep instanceof Webconnection) (headers.'webconnection-id' = activityOrStep.id) 
		else (headers.'webconnectionStep-id' = activityOrStep.id)
		changeMessageOwnerDetail(activityOrStep, message, Webconnection.OWNERDETAIL_PENDING)
		sendMessageAndHeaders("seda:activity-${activityOrStep.shortName}-${activityOrStep.id}", null, headers)
	}

 	def retryFailed(Webconnection c) {
 		TextMessage.findAllByMessageOwner(c).each {
 			if(it.ownerDetail == Webconnection.OWNERDETAIL_FAILED)
 				doUpload(c, it)
 		}
 	}

	def saveInstance(Webconnection webconnectionInstance, params) {
		webconnectionInstance.keywords?.clear()
		webconnectionInstance.name = params.name
		webconnectionInstance.initialize(params)
		webconnectionInstance.save(failOnError:true)

		webconnectionInstance.keywords?.clear()
		webconnectionInstance.save(flush:true, failOnError:true)
		if (params.sorting == 'disabled') {
			log.info "##### WebconnectionService.saveInstance() # removing keywords"
		} else if(params.sorting == 'global') {
			webconnectionInstance.addToKeywords(new Keyword(value:'', isTopLevel:true))
		} else if(params.sorting == 'enabled') {
			def keywords = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(',')
			keywords.collect { new Keyword(value:it.trim(), isTopLevel:true) }.each { webconnectionInstance.addToKeywords(it) }
		}
		webconnectionInstance.save(failOnError:true, flush:true)
	}

	def getStatusOf(Webconnection w) {
		camelContext.routes.any { it.id ==~ /.*activity-${w.shortName}-${w.id}$/ } ? ConnectionStatus.CONNECTED : ConnectionStatus.FAILED
	}

 	private changeMessageOwnerDetail(TextMessage message, String s) {
 		message.setMessageDetail(message.messageOwner, s)
 		message.save(failOnError:true, flush:true)
 		log.info "Changing Status ${message.ownerDetail}"
 	}

	def apiProcess(webcon, controller) {
		controller.render(generateApiResponse(webcon, controller))
	}

	def activate(activityOrStep) {
		createRoute(activityOrStep, activityOrStep.routeDefinitions)
	}

	def deactivate(activityOrStep) {
		log.info "################ Deactivating Webconnection :: ${activityOrStep}"
		camelContext.stopRoute("activity-${activityOrStep.shortName}-${activityOrStep.id}")
		camelContext.removeRoute("activity-${activityOrStep.shortName}-${activityOrStep.id}")
	}

	def generateApiResponse(webcon, controller) {
		def message = controller.request.JSON?.message
		def recipients = controller.request.JSON?.recipients
		def secret = controller.request.JSON?.secret
		def errors = [invalid:[], missing:[]]
		log.info "JSON IS ${controller.request.JSON}"
		log.info "MESSAGE IS ${controller.request.JSON?.message}"
		log.info "RECIPIENTS IS ${controller.request.JSON?.recipients}"

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
			log.info "errorMessage ::: $errorMessage"
			return [status:400, text:errorMessage]
		}

		//> Populate destinations
		log.info "evaluating the destinations for $recipients ...."
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
		log.info "groups: $groups. Addresses: $addresses"

		//> Send message
		def m = messageSendService.createOutgoingMessage([messageText: message, addresses: addresses, groups: groups])
		log.info "I am about to send $m"
		if(!m.dispatches) {
			return [status:400, text:"no recipients supplied"]
		}
		webcon.addToMessages(m)
		messageSendService.send(m)
		"message successfully queued to send to ${m.dispatches.size()} recipient(s)"
	}
}

