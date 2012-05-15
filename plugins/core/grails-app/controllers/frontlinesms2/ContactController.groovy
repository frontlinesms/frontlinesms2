package frontlinesms2

import grails.converters.JSON

class ContactController {
//> STATIC PROPERTIES
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

//> SERVICES
	def grailsApplication
	def contactSearchService

//> INTERCEPTORS
	def beforeInterceptor = {
		params.max = params.max?: grailsApplication.config.grails.views.pagination.max
		params.sort = params.sort ?: 'name'
		params.offset = params.offset ?: 0
		true
	}

//> ACTIONS
	def index = {
		redirect action: "show", params:params
	}
	
	def updateContactPane = {
		def contactInstance = Contact.get(params.id)
		def model = [contactInstance: contactInstance,
				contactGroupInstanceList: contactInstance?.groups ?: [],
				contactFieldInstanceList: contactInstance?.customFields ?: [],
				contactGroupInstanceTotal: contactInstance?.groups?.size() ?: 0,
				nonContactGroupInstanceList: contactInstance ? Group.findAllWithoutMember(contactInstance) : null,
				uniqueFieldInstanceList: CustomField.getAllUniquelyNamed(),
				fieldInstanceList: CustomField.findAll(),
				groupInstanceList: Group.findAll(),
				groupInstanceTotal: Group.count(),
				smartGroupInstanceList: SmartGroup.list()]
		render view:'/contact/_single_contact_view', model:model
	}
	
	def show = {
		if(params.flashMessage) {
			flash.message = params.flashMessage
		}
		def contactList = contactSearchService.contactList(params)
		def contactInstanceList = contactList.contactInstanceList
		def contactInstanceTotal = contactList.contactInstanceTotal
		def contactInstance = (params.contactId ? Contact.get(params.contactId) : (contactInstanceList[0] ?: null))
		def contactGroupInstanceList = contactInstance?.groups ?: []
		def contactFieldInstanceList = contactInstance?.customFields ?: []
		[contactInstance: contactInstance,
				checkedContactList: ',',
				contactInstanceList: contactInstanceList,
				contactInstanceTotal: contactInstanceTotal,
				contactsSection: contactList.contactsSection,
				contactFieldInstanceList: contactFieldInstanceList,
				contactGroupInstanceList: contactGroupInstanceList,
				contactGroupInstanceTotal: contactGroupInstanceList.size(),
				nonContactGroupInstanceList: contactInstance ? Group.findAllWithoutMember(contactInstance) : null,
				uniqueFieldInstanceList: CustomField.getAllUniquelyNamed(),
				fieldInstanceList: CustomField.findAll(),
				groupInstanceList: Group.findAll(),
				groupInstanceTotal: Group.count(),
				smartGroupInstanceList: SmartGroup.list()]
	}
	
	def createContact = {
		render view:'show', model: [contactInstance: new Contact(params),
				contactFieldInstanceList: [],
				contactGroupInstanceList: [],
				contactGroupInstanceTotal: 0,
				nonContactGroupInstanceList: Group.findAll(),
				uniqueFieldInstanceList: CustomField.getAllUniquelyNamed(),
				fieldInstanceList: CustomField.findAll(),
				groupInstanceList: Group.findAll(),
				groupInstanceTotal: Group.count(),
				smartGroupInstanceList: SmartGroup.list()] << contactSearchService.contactList(params)
	}

	def saveContact = {
		def contactInstance = Contact.get(params.contactId) ?: new Contact()
		contactInstance.properties = params
		if(attemptSave(contactInstance)) {
			parseContactFields(contactInstance)
			if(attemptSave(contactInstance))
				redirect(action:'show', params:[contactId:contactInstance.id])
			else
				redirect(action:'show')
		}
		else
			redirect(action:'show')
	}
	
	def update = {
		withContact { contactInstance ->
			contactInstance.properties = params
			parseContactFields(contactInstance)
			attemptSave(contactInstance)
			if(params.groupId) redirect(controller: params.contactsSection, action: 'show', id: params.groupId, params:[contactId: contactInstance.id, sort:params.sort, offset: params.offset])
			else redirect(action:'show', params:[contactId: contactInstance.id, offset:params.offset], max:params.max)
		}
	}
	
	def updateMultipleContacts = {
		params['contact-select']?.each { id ->
			withContact id, { contactInstance ->
				parseContactFields(contactInstance)
				attemptSave(contactInstance)
			}
		}
		render(view:'show', model: show())
	}
	
	def confirmDelete = {
		def contactIds = params.checkedContactList ? params.checkedContactList.tokenize(',').unique() : [params.contactId]
		def contactInstanceList = []
		contactIds.each { id ->
			withContact id, { contactInstance ->
				contactInstanceList << contactInstance
			}
		}
		println "conbtact List: $contactInstanceList"
		[contactInstanceList: contactInstanceList,
				contactInstanceTotal: contactInstanceList.size()]
	}
	
	def delete = {
		def contactIds = params.checkedContactList ? params.checkedContactList.tokenize(',').unique() : [params.contactId]
		contactIds.each { id ->
			withContact id, { contactInstance ->
				Contact.get(contactInstance.id).delete()
			}
		}
		flash.message = message(code: 'default.deleted.message', args: [message(code: 'contact.label', default: 'Contact'), ''])
		redirect(action: "show")		
	}

	def newCustomField = {
		def contactInstance = params.contactId
		def customFieldInstance = new CustomField()
		customFieldInstance.properties = params
		[customFieldInstance: customFieldInstance,
				contactInstance: contactInstance]
	}

	def search = {
		render template:'search_results', model:contactSearchService.contactList(params)
	}
	
	def checkForDuplicates = {
		def foundContact = Contact.findByMobile(params.number)
		if (foundContact && (foundContact.id.toString() != params.contactId))
			render(text: message(code: 'contact.exists.prompt'))
		else
			render ""
	}
	
	def getMessageStats = {
		def contactInstance = Contact.get(params.id)
		if(contactInstance) {
			def messageStats = [inboundMessagesCount: contactInstance.inboundMessagesCount, outboundMessagesCount: contactInstance.outboundMessagesCount]
			render messageStats as JSON
		}
	}

//> PRIVATE HELPER METHODS
	private def attemptSave(contactInstance) {
		def existingContact = params.mobile ? Contact.findByMobileLike(params.mobile) : null
		if (contactInstance.save()) {
			flash.message = message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.name])
			def redirectParams = [contactId: contactInstance.id]
			if(params.groupId) redirectParams << [groupId: params.groupId]
			return true
		} else if (existingContact && existingContact != contactInstance) {
			// TODO generate link with g:link
			flash.message = "${message(code: 'contact.exists.warn')}  <a href='/frontlinesms2/contact/show/" + Contact.findByMobileLike(params.mobile)?.id + "'>${message(code: 'contact.view.duplicate')}</g:link>"
			return false
		}
		return false
	}
	
	private def withContact(contactId = params.contactId, Closure c) {
		def contactInstance = Contact.get(contactId)
		if(contactInstance) {
			c.call(contactInstance)
		} else {
			// flash.message = message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])
			c.call(new Contact())
		}
	}
	
	def multipleContactGroupList = {
		def contactIds = params.checkedContactList.tokenize(',').unique()
		def sharedGroupInstanceList = []
		def groupInstanceList = []
		contactIds.each { id ->
			withContact id, { contactInstance ->
				groupInstanceList << contactInstance.getGroups()
			}
		}
		sharedGroupInstanceList = getSharedGroupList(groupInstanceList)
		def nonSharedGroupInstanceList = getNonSharedGroupList(Group.findAll(), sharedGroupInstanceList)
		render(view: "_multiple_contact_view", model: [sharedGroupInstanceList: sharedGroupInstanceList,
			nonSharedGroupInstanceList: nonSharedGroupInstanceList])
	}
	
	private def getSharedGroupList(Collection groupList) {
		def groupIds = groupList*.id
		def sharedGroupIds = groupIds?.inject(groupIds[0]){ acc, current -> acc.intersect(current)}
		sharedGroupIds ? Group.createCriteria().list {
			'in' ("id", sharedGroupIds)
		} : []
	}
	
	private def getNonSharedGroupList(Collection groupList1, Collection groupList2) {
		def groupIdList1 = groupList1.collect {it.id}
		def groupIdList2 = groupList2.collect {it.id}
		def nonSharedGroupList = (groupIdList1 - groupIdList2).collect { Group.findById(it) } ?: []
		nonSharedGroupList
	}

	private def parseContactFields(Contact contactInstance) {
		updateCustomFields(contactInstance)
		updateGroups(contactInstance)
		return contactInstance.stripNumberFields()
	}
	
	private def updateGroups(Contact contactInstance) {
		def groupsToAdd = params.groupsToAdd.tokenize(',').unique()
		def groupsToRemove = params.groupsToRemove.tokenize(',')
		
		// Check for errors in groupsToAdd and groupsToRemove
		if(!groupsToAdd.disjoint(groupsToRemove)) {
			contactInstance.errors.reject(message(code: 'contact.addtogroup.error'))
			return false
		}
		
		groupsToAdd.each() { id ->
			contactInstance.addToGroups(Group.get(id))
		}
		groupsToRemove.each() { id ->
			contactInstance.removeFromGroups(Group.get(id))
		}
		return contactInstance
	}
	
	private def updateCustomFields(Contact contactInstance) {
		def fieldsToAdd = params.fieldsToAdd ? params.fieldsToAdd.tokenize(',') : []
		def fieldsToRemove = params.fieldsToRemove ? params.fieldsToRemove.tokenize(',') : []
		
		fieldsToAdd.each() { name ->
			def existingFields = CustomField.findAllByNameAndContact(name, contactInstance)
			def fieldsByName = params."$name"
			if(fieldsByName?.class != String) {
				fieldsByName.each() { val ->
					if(val != "" && !existingFields.value.contains(val))
						contactInstance.addToCustomFields(new CustomField(name: name, value: val)).save(flush:true)
						existingFields = CustomField.findAllByNameAndContact(name, contactInstance)
				}
			} else if(fieldsByName != "" && !existingFields.value.contains(fieldsByName)) {
				contactInstance.addToCustomFields(new CustomField(name: name, value: fieldsByName))
			}
		}
		fieldsToRemove.each() { id ->
			def toRemove = CustomField.get(id)
			contactInstance.removeFromCustomFields(toRemove)
			if(toRemove)
				toRemove.delete()
		}
		return contactInstance
	}
}
