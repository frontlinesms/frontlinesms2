package frontlinesms2

import frontlinesms2.*

class AutoforwardService {

    def saveInstance(Autoforward autoforward, params) {
    	println "##### Saving Autoforward in Service"
		autoforward.name = params.name ?: autoforward.name
		autoforward.sentMessageText = params.messageText ?: autoforward.sentMessageText
		autoforward.keywords?.clear()
		editContacts(autoforward, params)
		println "##Just about to save"
		autoforward.save(flush:true, failOnError:true)
		println "##Just saved round 1"
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
		println "# 2 ######### Saving Round 2 # $autoforward.errors.allErrors"
		autoforward.save(failOnError:true,flush:true)
		println autoforward.contacts
		println autoforward.groups
		println autoforward.smartGroups
		return autoforward
	}

	def editContacts(autoforward, params){
		try{
			def newContacts = params.addresses.collect { return Contact.findByMobile(it)?:new Contact(mobile:it, name:'').save(failOnError:true) }
			println "#### Contacts from the form : ${newContacts}"
			(autoforward.contacts?:[] - newContacts?:[]).each { autoforward.removeFromContacts(it) }
			autoforward.save(failOnError:true)
			(newContacts?:[] - autoforward.contacts?:[]).each { autoforward.addToContacts(it) }

			def newGroups = []
			def newSmartGroups = []
			params.groups.each{
				if(it.startsWith('group')){
					newGroups << Group.get(it.substring(6,it.length()))
				} else {
					newSmartGroups << SmartGroup.get(it.substring(11,it.length()))
				}
			}

			(autoforward.groups?:[] - newGroups?:[]).each{ autoforward.removeFromGroups(it) }
			println "Remaining groups # $autoforward.groups"
			println "To add into Groups ${(newGroups?:[] - autoforward.groups?:[])}"
			autoforward.save(failOnError:true)
			(newGroups?:[] - autoforward.groups?:[]).each{ autoforward.addToGroups(it) }
			println "Adding groups # $autoforward.groups"

			(autoforward.smartGroups?:[] - newSmartGroups?:[]).each{ autoforward.removeFromSmartGroups(it) }
			println "Remaining smartGroups # $autoforward.smartGroups"
			println "To add to smartGroups ${(newSmartGroups?:[] - autoforward.smartGroups?:[])}"
			autoforward.save(failOnError:true)
			(newSmartGroups?:[] - autoforward.smartGroups?:[]).each{ autoforward.addToSmartGroups(it) }
			println "Adding smartGroups # $autoforward.smartGroups"

		} catch(Exception e) {
			println "# 1 ######### $autoforward.errors.allErrors"
			println "# Can't Change Contacts,Groups,SmartGroups # $e"
		}
		autoforward
	}
}