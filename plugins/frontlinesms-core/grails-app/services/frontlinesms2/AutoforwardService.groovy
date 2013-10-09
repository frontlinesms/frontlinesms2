package frontlinesms2

import grails.events.Listener
import groovy.sql.Sql

// TODO fix indentation of this class
class AutoforwardService {
	def messageSendService
	def dataSource

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
		def recipients = params.recipients
		try{
			def oldContacts = autoforward.contacts?:[]
			def oldGroups = autoforward.groups?:[]
			def oldSmartGroups = autoforward.smartGroups?:[]

			def newGroups = []
			def newSmartGroups = []
			def newContacts = []

			if (recipients) {
				println "I got here, people."
				newContacts = messageSendService.getContacts(recipients)
				newContacts += messageSendService.getManualAddresses(recipients).collect { return Contact.findByMobile(it)?:new Contact(mobile:it, name:'').save(failOnError:true) }

				messageSendService.getGroups(recipients).each { newGroups << it }
				messageSendService.getSmartGroups(recipients).each { newSmartGroups << it }
			}

			(oldContacts - newContacts?:[]).each { autoforward.removeFromContacts(it) }
			(newContacts?:[] - oldContacts).each { autoforward.addToContacts(it) }

			(oldGroups - newGroups?:[]).each{ autoforward.removeFromGroups(it) }
			(newGroups?:[] - oldGroups).each{ autoforward.addToGroups(it) }

			(oldSmartGroups - newSmartGroups?:[]).each{ autoforward.removeFromSmartGroups(it) }
			(newSmartGroups?:[] - oldSmartGroups).each{ autoforward.addToSmartGroups(it) }

		} catch(Exception e) {
			println "# 1 ######### $autoforward.errors.allErrors"
			println "# Can't Change Contacts,Groups,SmartGroups # $e"
		}
		autoforward
	}

	def doForward(autoforwardOrStep, message) {
		def m
		if(autoforwardOrStep instanceof Activity) {
			m = messageSendService.createOutgoingMessage([contacts:autoforwardOrStep.contacts, groups:autoforwardOrStep.groups?:[] + autoforwardOrStep.smartGroups?:[], messageText:autoforwardOrStep.sentMessageText])
			autoforwardOrStep.addToMessages(m)
			autoforwardOrStep.addToMessages(message)
		} else {
			m = messageSendService.createOutgoingMessage([addresses:autoforwardOrStep.recipients , messageText:autoforwardOrStep.sentMessageText])
		}
		message.messageOwner.addToMessages(m)
		message.messageOwner.save(failOnError:true)
		m.setMessageDetail(autoforwardOrStep, message.id)
		messageSendService.send(m)
		
		autoforwardOrStep.save(failOnError:true)
	}

	@Listener(topic='beforeDelete', namespace='gorm')
	def handleDeletedContact(Contact contact) {
		// TODO document why this is using raw SQL
		println "### Removing Contact $contact from Autoforward ##"
	    new Sql(dataSource).execute('DELETE FROM autoforward_contact WHERE contact_id=?', [contact.id])
	    return true
	}

	@Listener(topic='beforeDelete', namespace='gorm')
	def handleDeletedGroup(Group group) {
		// TODO document why this is using raw SQL
		println "## Removing Group $group From Autoforward"
	    new Sql(dataSource).execute('DELETE FROM autoforward_grup WHERE group_id=?', [group.id])
	    return true
	}

	@Listener(topic='beforeDelete', namespace='gorm')
	def handleDeletedSmartGroup(SmartGroup smartGroup) {
		// TODO document why this is using raw SQL
		println "## Removing SmartGroup $smartGroup From Autoforward"
	    new Sql(dataSource).execute('DELETE FROM autoforward_smart_group WHERE smart_group_id=?', [smartGroup.id])
	    return true
	}
}
