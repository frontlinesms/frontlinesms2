package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Processor
import java.util.regex.Pattern
import java.text.Normalizer

class SubscriptionService implements Processor {
	static transactional = false

	void process(Exchange exchange) {
		Fmessage message = getMessage(exchange)
		def address = message.src
		def msgText = message.text
		def keyword = getKeyword(msgText.trim())
		def groupToAdd = Group.findBySubscriptionKey(keyword)
		def groupToRemove = Group.findByUnsubscriptionKey(keyword)
		groupToAdd?.addToMembers(findOrCreateContact(address))
		groupToRemove?.removeFromMembers(findOrCreateContact(address))
		groupToAdd?.save()
		groupToRemove?.save()
	}

	private Contact findOrCreateContact(String src) {
		return Contact.findByPrimaryMobileOrSecondaryMobile(src, src) ?: new Contact(primaryMobile: src)
	}

	private def getKeyword(message) {
		def keyword =""
		message.find {
			if (!Character.isLetterOrDigit(it.toCharacter())) return true
				keyword += it
				return false
		}
		return keyword
	}

	Fmessage getMessage(Exchange exchange) {
		return new Fmessage()
	}
}