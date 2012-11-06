package frontlinesms2

import frontlinesms2.*

class AutoforwardService {

    def saveInstance(Autoforward autoforward, params) {
		autoforward.name = params.name ?: autoforward.name
		autoforward.sentMessageText = params.messageText ?: autoforward.sentMessageText
		autoforward.keywords?.clear()

		def newContacts = params.addresses.collect { return Contact.findByMobile(it)?:new Contact(mobile:it).save() }
		(autoforward.contacts - newContacts).each { autoforward.removeFromContacts(it) }
		(newContacts - autoforward.contacts).each { autoforward.addToContacts(it) }

		def newGroups = params.groups.collect { return Group.get(it.substring(it.length()-2,it.length()-1)) }

		((autoforward.groups + autoforward.smartGroups) - newGroups).each{
			if(group instanceof SmartGroup){ autoforward.removeFromSmartGroups(group) }
			else if (group instanceof Group){ autoforward.removeFromGroups(group) }
		}

		(newGroups - (autoforward.groups + autoforward.smartGroups)).each{
			if(group instanceof SmartGroup){ autoforward.addToSmartGroups(group) }
			else if (group instanceof Group){ autoforward.addToGroups(group) }
		}

		autoforward.save(flush:true, failOnError:true)

		if(params.sorting == 'global'){
			autoforward.addToKeywords(new Keyword(value:''))
		}else if(params.sorting == 'enabled'){
			def keywordRawValues = params.keywords?.toUpperCase().replaceAll(/\s/, "").split(",")
			for(keywordValue in keywordRawValues) {
				def keyword = new Keyword(value: keywordValue.trim().toUpperCase())
				autoforward.addToKeywords(keyword)
			}
		} else {
			println "##### AutoforwardService.saveInstance() # removing keywords"
		}
		autoforward.save(failOnError:true,flush:true)
		return autoforward
	}
}
