package frontlinesms2

class MessageSendService {	
	def send(Fmessage m, Fconnection c=null) {
		println "MessageSendService.send($m, $c)"
		assert m instanceof Fmessage
		m.status = MessageStatus.SEND_PENDING
		def headers = [:]
		if(c) headers.fconnection = c.id
		sendMessageAndHeaders('seda:outgoing-fmessages', m, headers)
		println 'should be queued :)'
	}
}
