package frontlinesms2

class MessageSendService {
	def send(Fmessage m, Fconnection c=null) {
		assert m instanceof Fmessage
		m.status = MessageStatus.SEND_PENDING
		def headers = [:]
		if(c) headers.fconnection = c.id
		m.save(failOnError:true,flush:true) // FIXME this should be saving inside the outgoing messages route, not here
		sendMessageAndHeaders('seda:outgoing-fmessages', m, headers)
	}
	
	def getMessagesToSend(params) {
		def messages = []
		def addresses = [params.addresses].flatten() - null
		def groups = [params.groups].flatten() - null
		addresses += groups.collect {
			println it
			Group.findByName(it).getAddresses()
		}.flatten()
		addresses.unique().each { address ->
			//TODO: Need to add source from app setting
			messages << new Fmessage(src: "src", dst: address, text: params.messageText)
		}
		return messages
	}
}
