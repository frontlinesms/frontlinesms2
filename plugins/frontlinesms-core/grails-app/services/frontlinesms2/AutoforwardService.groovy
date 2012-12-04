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
			if(params.addresses){
				def newContacts = [params.addresses].flatten().collect { return Contact.findByMobile(it)?:new Contact(mobile:it, name:'').save(failOnError:true) }
				def oldContacts = autoforward.contacts?:[]
				(oldContacts - newContacts?:[]).each { autoforward.removeFromContacts(it) }
				(newContacts?:[] - oldContacts).each { autoforward.addToContacts(it) }
			}

			def newGroups = []
			def newSmartGroups = []

			if(params.groups){
				([params.groups].flatten() - null).each{
					if(it?.startsWith('group')){
						newGroups << Group.get(it.substring(it.indexOf('-')+1))
					} else if (it?.startsWith('smartgroup')) {
						newSmartGroups << SmartGroup.get(it.substring(it.indexOf('-')+1))
					}
				}
			}

			def oldGroups = autoforward.groups?:[]
			(oldGroups - newGroups?:[]).each{ autoforward.removeFromGroups(it) }
			(newGroups?:[] - oldGroups).each{ autoforward.addToGroups(it) }
			def oldSmartGroups = autoforward.smartGroups?:[]
			(oldSmartGroups - newSmartGroups?:[]).each{ autoforward.removeFromSmartGroups(it) }
			(newSmartGroups?:[] - oldSmartGroups).each{ autoforward.addToSmartGroups(it) }

		} catch(Exception e) {
			println "# 1 ######### $autoforward.errors.allErrors"
			println "# Can't Change Contacts,Groups,SmartGroups # $e"
		}
		autoforward
	}
	def handleDeleteContact(contact) {
		
	}
}
