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
}
