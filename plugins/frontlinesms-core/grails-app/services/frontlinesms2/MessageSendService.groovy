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
		def addresses = []
		if (params.recipients) {
			addresses = getAddressesFromRecipientList(params.recipients)
		} else {
			addresses = [params.addresses].flatten() - null
			addresses += getAddressesForContacts(params.contacts)
			addresses += getAddressesForGroups([params.groups].flatten())
		}

		def dispatches = generateDispatches(addresses)
		dispatches.each {
			message.addToDispatches(it)
		}
		return message
	}

	private def getAddressesForContacts(contacts) {
		if(contacts) contacts*.mobile
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

	def getAddressesFromRecipientList(recipients) {
		def addresses = []
		def contactList
		def groupAddressList
		def smartGroupAddressList
		def manualAddressList
		def stripPrefix = { it.tokenize('-')[1] }

		contactList = recipients.findAll { it.startsWith('contact') }.collect { Contact.get(stripPrefix(it))?.mobile }.findAll { it!=null }

		groupAddressList = recipients.findAll { it.startsWith('group') }
			.collect { Group.get(stripPrefix(it)).addresses }
			.flatten()

		smartGroupAddressList = recipients.findAll { it.startsWith('smartgroup') }
			.collect { SmartGroup.get(stripPrefix(it)).addresses }
			.flatten()

		manualAddressList = recipients.findAll { it.startsWith('address') }.collect { stripPrefix(it) }

		println "contactList: $contactList"
		println "groupAddressList: $groupAddressList"
		println "smartGroupAddressList: $smartGroupAddressList"
		println "manualAddressList: $manualAddressList"

		addresses = contactList + groupAddressList + smartGroupAddressList + manualAddressList
		println "addresses: $addresses"
		addresses.unique()
	}
}

