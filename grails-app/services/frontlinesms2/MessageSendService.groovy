package frontlinesms2

import org.apache.camel.Exchange

class MessageSendService {
    static transactional = true

    def dispatch(Fmessage m, SmslibFconnection c) {
		m.save(failOnError:true, flush:true)
		sendMessage('seda:smslib-outgoing-fmessages', [body:m, endpoint:c.camelAddress()])
    }
}
