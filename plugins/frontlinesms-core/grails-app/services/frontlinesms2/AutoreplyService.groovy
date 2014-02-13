package frontlinesms2

class AutoreplyService {
	def messageSendService
	def saveInstance(Autoreply autoreply, params) {
		autoreply.name = params.name ?: autoreply.name
		autoreply.autoreplyText = params.messageText ?: autoreply.autoreplyText
		autoreply.keywords?.clear()
		autoreply.save(flush:true, failOnError:true)

		if(params.sorting == 'global') {
			autoreply.addToKeywords(new Keyword(value:''))
		} else if(params.sorting == 'enabled') {
			def keywordRawValues = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(",")
			for(keywordValue in keywordRawValues) {
				def keyword = new Keyword(value: keywordValue.trim().toUpperCase())
				autoreply.addToKeywords(keyword)
			}
		} else {
			log.info "##### AutoreplyService.saveInstance() # removing keywords"
		}
		autoreply.save(failOnError:true, flush:true)
	}

	def doReply(activityOrStep, message) {
		def autoreplyText
		if (activityOrStep instanceof Activity) {
			autoreplyText = activityOrStep.autoreplyText
		}
		else if (activityOrStep instanceof Step) {
			autoreplyText = activityOrStep.getPropertyValue('autoreplyText')
		}
		def params = [:]
		params.addresses = message.src
		params.messageText = autoreplyText
		def outgoingMessage = messageSendService.createOutgoingMessage(params)
		message.messageOwner.addToMessages(outgoingMessage)
		message.messageOwner.save(failOnError:true)
		outgoingMessage.setMessageDetail(activityOrStep, message.id)
		outgoingMessage.save(failOnError:true)
		
		messageSendService.send(outgoingMessage)
		activityOrStep.save()
	}
}

