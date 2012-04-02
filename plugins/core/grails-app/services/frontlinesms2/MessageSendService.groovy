package frontlinesms2

class MessageSendService {
	static transactional = false
	
	def send(Fmessage m, Fconnection c=null) {
		def headers = [:]
		if(c) headers.fconnection = c.id
		m.save()
		m.dispatches.each {
			sendMessageAndHeaders('seda:dispatches', it, headers)
		}
	}
	
	def retry(Fmessage m) {
		m.dispatches.each { dispatch ->
			if(dispatch.status == DispatchStatus.FAILED) {
				sendMessage('seda:dispatches', dispatch)
			}
		}
	}
	
	def getMessagesToSend(params) {
		//TODO: Need to add source from app setting
		def message = new Fmessage(src: 'src', date: new Date(), text: params.messageText, inbound: false, hasPending: true)
		def dispatches = []
		def addresses = [params.addresses].flatten() - null
		def groups = [params.groups].flatten() - null
		addresses += groups.collect {
			println it
			Group.findByName(it) ? Group.findByName(it).getAddresses() : SmartGroup.findByName(it).getAddresses()
		}.flatten()
		addresses.unique().each { address ->
			dispatches << new Dispatch(dst: address, status: DispatchStatus.PENDING)
		}
		dispatches.each {
			message.addToDispatches(it)
		}
		return message
	}
}
