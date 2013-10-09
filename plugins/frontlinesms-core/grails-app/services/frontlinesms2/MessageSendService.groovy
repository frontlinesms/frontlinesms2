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
		print "Outgoing params: $params"
		def message = new Fmessage(text:(params.messageText), inbound:false)
		def addresses = []
		if (params.recipients) {
			addresses = getAddressesFromRecipientList(params.recipients)
			println "addresses: $addresses"
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

	def stripPrefix = { it.tokenize('-')[1] }

	def getContacts = { recipients ->
		recipients.findAll { it.startsWith('contact') }.collect { Contact.get(stripPrefix(it)) }.findAll { it!=null }
	}

	def getGroups = { recipients ->
		recipients.findAll { it.startsWith('group') }.collect { Group.get(stripPrefix(it)) }.flatten()
	}

	def getSmartGroups = { recipients ->
		recipients.findAll { it.startsWith('smartgroup') }.collect { SmartGroup.get(stripPrefix(it)) }.flatten()
	}

	def getManualAddresses = { recipients ->
		recipients.findAll { it.startsWith('address') }.collect { stripPrefix(it) }
	}

	def getAddressesFromRecipientList(rawRecipients) {
		def recipients = [rawRecipients].flatten()
		def addresses = []
		def contactList = []
		def groupAddressList = []
		def smartGroupAddressList = []
		def manualAddressList = []

		contactList = getContacts(recipients)*.mobile.flatten() - null
		groupAddressList = getGroups(recipients)*.addresses.flatten() - null
		smartGroupAddressList = getSmartGroups(recipients)*.addresses.flatten() - null
		manualAddressList = getManualAddresses(recipients).flatten()

		println "contactList: $contactList"
		println "groupAddressList: $groupAddressList"
		println "smartGroupAddressList: $smartGroupAddressList"
		println "manualAddressList: $manualAddressList"

		addresses = contactList + groupAddressList + smartGroupAddressList + manualAddressList
		println "Addresses: $addresses"
		addresses.flatten().unique()
	}
}

