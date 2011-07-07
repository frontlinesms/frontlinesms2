package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Processor

class SubscriptionService implements Processor {
	static transactional = false

	void process(Exchange exchange) {
		Fmessage message = getMessage(exchange)
		def address = message.src
		def firstWord = message.text.split(" ")[0]
		def groupToAdd = Group.findBySubscriptionKey(firstWord)
		def groupToRemove = Group.findByUnsubscriptionKey(firstWord)
		groupToAdd?.addToMembers(findOrCreateContact(address))
		groupToRemove?.removeFromMembers(findOrCreateContact(address))
		groupToAdd?.save()
		groupToRemove?.save()
	}

	private Contact findOrCreateContact(String src) {
		return Contact.findByPrimaryMobileOrSecondaryMobile(src, src) ?: new Contact(primaryMobile: src)
	}
	
	 Fmessage getMessage(Exchange exchange) {
		return new Fmessage()
	}
}