package frontlinesms2

import frontlinesms2.*

class AutoforwardService {

    def saveInstance(Autoforward autoforward, params) {
    	println "##### Saving Autoforward in Service"
		autoforward.name = params.name ?: autoforward.name
		autoforward.autoforwardText = params.messageText ?: autoforward.autoforwardText
		autoforward.keywords?.clear()
		def newContacts = params.addresses.collect { return Contact.findByMobile(it)?:new Contact(mobile:it).save(failOnError:true) }
		println "#### Contacts from the form : ${newContacts}"
		autoforward.removeFromContacts(autoforward.contacts - newContacts)
		autoforward.addToContacts(newContacts - autoforward.contacts)
		println "##### All of the contacts : ${autoforward.contacts}"

		def newGroups = params.groups.collect { return Group.get(it.substring(it.length()-2,it.length()-1)) }

		((autoforward.groups + autoforward.smartGroups) - newGroups).each{
			if(group instanceof SmartGroup){ autoforward.removeFromSmartGroups(group) }
			else if (group instanceof Group){ autoforward.removeFromGroups(group) }
		}
		println "Changed smart groups"
		(newGroups - (autoforward.groups + autoforward.smartGroups)).each{
			if(group instanceof SmartGroup){ autoforward.addToSmartGroups(group) }
			else if (group instanceof Group){ autoforward.addToGroups(group) }
		}
		println "# 1 ######### $autoforward.errors.allErrors"
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
		println "# 2 ######### $autoforward.errors.allErrors"
		autoforward.save(failOnError:true,flush:true)
		return autoforward
	}
}
