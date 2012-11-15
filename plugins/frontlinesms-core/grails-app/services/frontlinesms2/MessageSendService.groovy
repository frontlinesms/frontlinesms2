package frontlinesms2

class MessageSendService {
	static transactional = false
	
	def send(Fmessage m, Fconnection c=null) {
		def headers = [:]
		if(c) headers['requested-fconnection-id'] = c.id
		m.save()
		m.dispatches.each {
			sendMessageAndHeaders('seda:dispatches', it, headers)
		}
	}
	
	def retry(Fmessage m) {
		def dispatchCount = 0
		m.dispatches.each { dispatch ->
			if(dispatch.status == DispatchStatus.FAILED) {
				sendMessage('seda:dispatches', dispatch)
				++dispatchCount
			}
		}
		return dispatchCount
	}
	
	def createOutgoingMessage(params) {
		def message = new Fmessage(text:(params.messageText), inbound:false)
		def addresses = [params.addresses].flatten() - null
		def groups = [params.groups].flatten() - null
		addresses += getAddressesForGroups(groups)

		def dispatches = generateDispatches(addresses)
		dispatches.each {
			message.addToDispatches(it)
		}
		return message
	}

	def getAddressesForGroups(List groups) {
		groups.collect {
			def g = it
			if(g instanceof String || g instanceof GString) {
				if(it.startsWith('group-')) {
					g = Group.get(it.substring(6))
				} else if(it.startsWith('smartgroup-')) {
					g = SmartGroup.get(it.substring(11))
				}
			}
			g?.addresses
		}.flatten()
	}

	def generateDispatches(List addresses) {
		(addresses.unique() - null).collect {
			it = it.replaceAll(/\s|\(|\)|\-/, "")
			new Dispatch(dst:it, status:DispatchStatus.PENDING)
		}
	}
}

