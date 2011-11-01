package frontlinesms2

import grails.util.GrailsConfig

class ContactController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
	def contactSearchService

	def beforeInterceptor = {
		params.max = params.max?: GrailsConfig.config.grails.views.pagination.max
		params.sort = params.sort ?: 'name'
		params.offset = params.offset ?: 0
		true
	}

	def index = {
		redirect action: "show", params:params
	}

	def show = { contactInstance ->
		if(params.flashMessage) {
			flash.message = params.flashMessage
		}
		def contactList = contactSearchService.contactList(params)
		def contactInstanceList = contactList.contactInstanceList
		def contactInstanceTotal = contactList.contactInstanceTotal
		if (!contactInstance)
			contactInstance = (params.contactId ? Contact.get(params.contactId) : (contactInstanceList[0] ?: null))
		def contactGroupInstanceList = contactInstance?.groups ?: []
		def contactFieldInstanceList = contactInstance?.customFields ?: []
		[pageTitle: getPageTitle(),
				contactInstance: contactInstance,
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
	
	def update = {
		withContact { contactInstance ->
			if (params.version) { // TODO create withVersionCheck closure for use in all Controllers
				def version = params.version.toLong()
				if (contactInstance.version > version) {
					contactInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'contact.label', default: 'Contact')] as Object[], "Another user has updated this Contact while you were editing")
					render(view: "show", model: [contactInstance: contactInstance])
					return
				}
			}
			contactInstance.properties = params
			updateData(contactInstance)
			render(view:'show', model:show(contactInstance))
		}
	}
	
	def updateMultipleContacts = {
		if(params.checkedContactList) {
			def contactIds = params.checkedContactList.tokenize(',').unique()
			contactIds.each { id ->
				withContact id, { contactInstance ->
					updateData(contactInstance)
				}
			}
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), ''])}"
			render(view:'show', model: show())
		}
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
		def contactInstance = new Contact(params)
		contactInstance.properties = params
		updateData(contactInstance)
		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
		redirect(action:'show')
	}
	
	def confirmDelete = {
		def contactIds = params.checkedContactList ? params.checkedContactList.tokenize(',').unique() : [params.contactId]
		def contactInstanceList = []
		contactIds.each { id ->
			withContact id, { contactInstance ->
				contactInstanceList << contactInstance
			}
		}
		[contactInstanceList: contactInstanceList,
				contactInstanceTotal: contactInstanceList.count()]
	}
	
	def delete = {
		def contactIds = params.checkedContactList ? params.checkedContactList.tokenize(',').unique() : [params.contactId]
		contactIds.each { id ->
			withContact id, { contactInstance ->
				Contact.get(contactInstance.id).delete()
			}
		}
		flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'contact.label', default: 'Contact'), ''])}"
		redirect(action: "show")		
	}

	def newCustomField = {
		def contactInstance = params.contactId
		def customFieldInstance = new CustomField()
		customFieldInstance.properties = params
		[customFieldInstance: customFieldInstance,
				contactInstance: contactInstance]
	}

	def multipleContactGroupList = {
		if(!params.checkedContactList) {
			return []
		}
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
	
	def search = {
		render template: 'contact_details', model: contactSearchService.contactList(params)
	}
	
	private def getPageTitle() {
		if(params.smartGroupId) SmartGroup.get(params.smartGroupId)?.name
		else null
	}
	
	private def withContact(contactId=params.contactId, Closure c) {
		def contactInstance = Contact.get(contactId)
		if (contactInstance) c contactInstance
		else flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
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

	private def updateData(Contact contactInstance) {
		// Check for errors in groupsToAdd and groupsToRemove
		def groupsToAdd = params.groupsToAdd.tokenize(',').unique()
		def groupsToRemove = params.groupsToRemove.tokenize(',')
		
		def fieldsToAdd = params.fieldsToAdd ? params.fieldsToAdd.tokenize(',') : []
		def fieldsToRemove = params.fieldsToRemove ? params.fieldsToRemove.tokenize(',') : []
		if(!groupsToAdd.disjoint(groupsToRemove)) {
			contactInstance.errors.reject('Cannot add and remove from the same group!')
		} else if (contactInstance.validate() && !contactInstance.hasErrors()) {
			contactInstance.save(flush:true)
			groupsToAdd.each() { id ->
				contactInstance.addToGroups(Group.get(id))
			}
			groupsToRemove.each() { id ->
				contactInstance.removeFromGroups(Group.get(id))
			}

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
				if(toRemove)
					toRemove.delete(failOnError: true, flush:true)
			}
			contactInstance.stripNumberFields()
			
			if(contactInstance.save(flush:true)) {
				flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
				def redirectParams = [contactId:contactInstance.id]
				if(params.groupId) redirectParams << [groupId: params.groupId]
			}
		}
	}
}
