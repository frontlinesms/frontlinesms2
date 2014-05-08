package frontlinesms2

import org.springframework.transaction.annotation.Transactional
import org.apache.camel.Exchange
import grails.converters.JSON

class FrontlinesyncService {
	def fconnectionService
	public static final int OUTBOUND_MESSAGE_SUCCESS_CODE = 2

	def apiProcess(connection, controller) {
		def data = controller.request.JSON
		log.info "PARAMS : ${data}"
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

			data.payload.outboundTextMessageStatuses.each { msgStatus ->
				def d = Dispatch.get(msgStatus.dispatchId)
				if(msgStatus.deliveryStatus as int == OUTBOUND_MESSAGE_SUCCESS_CODE) {
					d?.status = DispatchStatus.SENT
					d?.dateSent = new Date()
				}
				else {
					d?.status = DispatchStatus.FAILED
				}
				d?.save(failOnError: true)
			}

			if(data.payload.config) {
				def config = data.payload.config
				updateSyncConfig(config, connection)
			}

			def payload
			def outgoingPayload = generateOutgoingResponse(connection)
			connection.lastConnectionTime = new Date()
			connection.save()
			payload = (outgoingPayload as JSON)
			controller.render text:payload
		} catch(Exception ex) {
			ex.printStackTrace()
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
		if(connection.sendEnabled) {
			def q = connection.hasDispatches ? connection.queuedDispatches : []
			if(q) {
				connection.removeDispatchesFromQueue()
				responseMap.messages = q.collect { d ->
					[to:d.dst, message:d.text, dispatchId:d.id]
				}
			}
		}
		if(!connection.configSynced) {
			responseMap.config = generateSyncConfig(connection)
			connection.configSynced = true
			connection.save()
		}
		if(responseMap.keySet().size() == 0) {
			responseMap.success =  true
		}
		responseMap
	}

	@Transactional
	public updateSyncConfig(config, connection, markAsDirty = true){
		["sendEnabled", "receiveEnabled", "missedCallEnabled"].each {
			connection."$it" = config."$it" as boolean
		}
		if(config.checkIntervalIndex) {
			connection.checkInterval = FrontlinesyncFconnection.checkIntervalOptions[config.checkIntervalIndex as int]
		}
		else if(config.checkInterval != null) {
			connection.checkInterval = config.checkInterval as Integer
		}
		connection.configSynced = markAsDirty
		if(connection.sendEnabled) {
			fconnectionService.enableFconnection(connection)
		}
		else {
			fconnectionService.destroyRoutes(connection)
		}
		connection.save()
	}

	private generateSyncConfig(connection) {
		def m = [:]
		["sendEnabled", "receiveEnabled", "missedCallEnabled", "checkInterval"].each {
			m."$it" = connection."$it"
		}
		m
	}

	private def failure(controller, message='ERROR', status=500) {
		controller.render text:message, status:status
	}
}

