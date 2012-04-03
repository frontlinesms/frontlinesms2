package frontlinesms2

class AutoreplyController extends ActivityController {

	def save = {
		// FIXME this should use withAutoreply to shorten and DRY the code, but it causes cascade errors as referenced here:
		// http://grails.1312388.n4.nabble.com/Cascade-problem-with-hasOne-relationship-td4495102.html
		def autoreply
		if(Autoreply.get(params.ownerId)) {
			autoreply = Autoreply.get(params.ownerId)
			autoreply.keyword ? autoreply.keyword.value = params.keyword : (autoreply['keyword'] = new Keyword(value: params.keyword))
			autoreply.name = params.name ?: autoreply.name
			autoreply.autoreplyText = params.autoreplyText ?: autoreply.autoreplyText
			autoreply.save(flush: true, failOnError: true)
		} else {
			def keyword = new Keyword(value: params.keyword)
			autoreply = new Autoreply(name: params.name, autoreplyText: params.autoreplyText, keyword: keyword)
			autoreply.save(flush: true, failOnError: true)
		}
		flash.message = "Autoreply has been saved!"
		[ownerId: autoreply.id]
	}
	
	def sendReply = {
		def autoreply = Autoreply.get(params.ownerId)
		def incomingMessage = Fmessage.get(params.messageId)
		params.addresses = incomingMessage.src
		params.messageText = autoreply.autoreplyText
		def outgoingMessage = messageSendService.createOutgoingMessage(params)
		autoreply.addToMessages(outgoingMessage)
		messageSendService.send(outgoingMessage)
		autoreply.save()
		render ''
	}
	
	private def withAutoreply(Closure c) {
		Autoreply.get(params.ownerId) ?: new Autoreply()
	}
}
