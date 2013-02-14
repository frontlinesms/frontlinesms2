package frontlinesms2

import grails.converters.JSON

import frontlinesms2.api.*

class NexmoService {
	def apiProcess(connection, controller) {
		controller.render(generateApiResponse(connection, controller) as JSON)
	}

	def generateApiResponse(connection, controller) {
		//if(connection.secret && controller.params.secret != connection.secret) return failure()

		try {
			def payload = handleIncoming(connection, controller.params)
			return [payload:payload]
		} catch(FrontlineApiException ex) {
			return failure(ex)
		}
	}

	private def handleIncoming(connection, params) {
		if(!connection.receiveEnabled) throw new FrontlineApiException("Receive not enabled for this connection")
		if(!params.msisdn || params.text==null) throw new FrontlineApiException('Missing one or both of `msisdn` and `text` parameters')
		/* parse received JSON with the following params:
			msisdn : sender of incoming message
			text : incoming message
		     */
		sendMessageAndHeaders('seda:incoming-fmessages-to-store',
				new Fmessage(inbound:true, src:params.msisdn, text:params.text),
				['fconnection-id':connection.id])

		def response = success()
		return response
	}

	private def failure(FrontlineApiException ex=null) {
		if(ex) {
			return [payload:[success:'false', error:ex.message]]
		} else {
			return [payload:[success:'false']]
		}
	}

	private def success(additionalContent=null) {
		def responseMap = [success:'true']
		if(additionalContent) responseMap += additionalContent
		return responseMap
	}
}