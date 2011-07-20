package frontlinesms2

class MessageSendService {
	static transactional = true

	def dispatch(Fmessage m, SmslibFconnection c) {
		m.save(failOnError:true, flush:true)
		sendMessageAndHeaders('seda:smslib-outgoing-fmessages', m, [fconnection:c.id])
	}
}
