package frontlinesms2

import org.springframework.transaction.annotation.Transactional
import org.apache.camel.Exchange
import grails.converters.JSON

class FrontlinesyncService {
	def apiProcess(connection, controller) {
		def data = controller.request.JSON
		if(connection.secret && data.secret != connection.secret) {
			return failure(controller, 'bad secret', 403)
		}

		try {
			data.payload.inboundTextMessages.each { e ->
				sendMessageAndHeaders('seda:incoming-fmessages-to-store',
						new TextMessage(inbound:true,
								src:e.fromNumber,
								text:e.text,
								date:new Date(e.smsTimestamp)),
						['fconnection-id':connection.id])
			}

			data.payload.missedCalls.each { e ->
				sendMessageAndHeaders('seda:incoming-missedcalls-to-store',
						new MissedCall(inbound:true,
								src:e.fromNumber,
								date:new Date(e.callTimestamp)),
						['fconnection-id':connection.id])
			}

			if(data.payload.config) {
				def config = data.payload.config
				updateSyncConfig(config, connection)
			}

			def payload
			if(connection.sendEnabled) {
				def outgoingPayload = generateOutgoingResponse(connection)
				payload = (outgoingPayload as JSON)
			} else {
				payload = ([success:true] as JSON)
			}
			controller.render text:payload
		} catch(Exception ex) {
			failure(controller, ex.message)
		}
	}

	@Transactional
	void processSend(Exchange x) {
		def connection = FrontlinesyncFconnection.get(x.in.headers['fconnection-id'])
		connection.addToQueuedDispatches(x.in.body)
		connection.save(failOnError:true)
	}

	@Transactional
	private generateOutgoingResponse(connection) {
		def responseMap = [:]
		def q = connection.queuedDispatches
		if(q) {
			connection.removeDispatchesFromQueue(q)
			responseMap.messages = q.collect { d ->
					d.status = DispatchStatus.SENT
					d.dateSent = new Date()
					d.save(failOnError: true)
					[to:d.dst, message:d.text]
				}
		}
		if(!connection.configSynced) {
			responseMap.config = generateSyncConfig(connection)
			connection.configSynced = true
			connection.save()
		}
		responseMap
	}

	@Transactional
	private updateSyncConfig(config, connection){
		["sendEnabled", "receiveEnabled", "missedCallEnabled"].each {
			connection."$it" = config."$it"
		}
		connection.configSynced = true
		connection.save()
	}

	private generateSyncConfig(connection) {
		def m = [:]
		["sendEnabled", "receiveEnabled", "missedCallEnabled"].each {
			m."$it" = connection."$it"
		}
		m
	}

	private def failure(controller, message='ERROR', status=500) {
		controller.render text:message, status:status
	}
}

