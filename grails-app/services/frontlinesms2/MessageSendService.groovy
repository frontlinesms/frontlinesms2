package frontlinesms2

class MessageSendService {
	def send(Fmessage m, Fconnection c=null) {
		assert m instanceof Fmessage
		m.hasPending = true
		def headers = [:]
		if(c) headers.fconnection = c.id
		m.save(failOnError:true,flush:true) // FIXME this should be saving inside the outgoing messages route, not here
		sendMessageAndHeaders('seda:outgoing-fmessages', m, headers)
	}
	
	def getMessagesToSend(params) {
		// FIXME this method should create 1 Fmessage with multiple Dispatches attached
		def messages = []
		def addresses = [params.addresses].flatten() - null
		def groups = [params.groups].flatten() - null
		addresses += groups.collect {
			println it
			Group.findByName(it) ? Group.findByName(it).getAddresses() : SmartGroup.findByName(it).getAddresses()
		}.flatten()
		addresses.unique().each { address ->
			//TODO: Need to add source from app setting
			messages << new Fmessage(src: "src", dst: address, text: params.messageText, dateCreated: new Date())
		}
		return messages
	}
}
