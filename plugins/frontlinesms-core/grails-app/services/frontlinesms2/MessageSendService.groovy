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
		addresses += getAddressesForContacts(params.contacts)
		addresses += getAddressesForGroups([params.groups].flatten())

		def dispatches = generateDispatches(addresses)
		dispatches.each {
			message.addToDispatches(it)
		}
		return message
	}

	private def getAddressesForContacts(contacts) {
		if(contacts) contacts*.mobile
	}

	private def getAddressesForGroups(List groups) {
		groups -= null
		groups.collect { g ->
			if(g instanceof String) {
				if(g.startsWith('group-')) {
					g = Group.get(g.substring(6))
				} else if(g.startsWith('smartgroup-')) {
					g = SmartGroup.get(g.substring(11))
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

