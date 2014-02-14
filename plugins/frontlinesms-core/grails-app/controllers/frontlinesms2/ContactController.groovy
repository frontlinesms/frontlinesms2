package frontlinesms2

import grails.converters.JSON


class ContactController extends ControllerUtils {
//> STATIC PROPERTIES
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

//> SERVICES
	def grailsApplication
	def contactSearchService
	def appSettingsService

//> INTERCEPTORS
	def beforeInterceptor = {
		def maxConfigured = grailsApplication.config.grails.views.pagination.max

		def maxRequested = {
			try {
				return params.max as Integer
			} catch(Exception _) {}
		}.call()?: Integer.MAX_VALUE
		params.max = Math.min(maxRequested, maxConfigured)
		params.sort = params.sort ?: 'name'
		params.offset = params.offset ?: 0
		true
	}

//> ACTIONS
	def index() {
		redirect action:'show', params:params
	}

	def getUniqueCustomFields() {
		def allFields = CustomField.getAllUniquelyNamed()
		withFormat {
			json {
				render([uniqueCustomFields:allFields] as JSON)
			}
		}
	}

	def disableWarning() {
		def warning = params.warning
		if(warning == "NonNumericNotAllowedWarning") {
			appSettingsService.set("non.numeric.characters.removed.warning.disabled", true)
		} else if(warning =="l10nWarning") {
			appSettingsService.set("international.number.format.warning.disabled", true)
		}
		render ([ok:true] as JSON)
	}

	def updateContactPane() {
		def contactInstance = Contact.get(params.id)
		def usedFields = contactInstance?.customFields ?: []
		def usedFieldNames = []
		usedFields.each() { field ->
			usedFieldNames.add(field.name)
		}
		def allFields = CustomField.getAllUniquelyNamed()
		def unusedFields = []
		allFields.each() {
			if(!usedFieldNames.contains(it))
				unusedFields.add(it)
		}
		def model = [contactInstance: contactInstance,
				contactGroupInstanceList: contactInstance?.groups ?: [],
				contactFieldInstanceList: usedFields,
				contactGroupInstanceTotal: contactInstance?.groups?.size() ?: 0,
				nonContactGroupInstanceList: contactInstance ? Group.findAllWithoutMember(contactInstance) : null,
				uniqueFieldInstanceList: unusedFields,
				fieldInstanceList: CustomField.findAll(),
				groupInstanceList: Group.findAll(),
				smartGroupInstanceList: SmartGroup.list()]
		render view:'/contact/_single_contact', model:model
	}

	def show() {
		def contactList = contactSearchService.contactList(params)
		def contactInstanceList = contactList.contactInstanceList
		def contactInstanceTotal = Contact.count()
		def contactInstance = (params.contactId ? Contact.get(params.contactId) : (contactInstanceList ? contactInstanceList[0] : null))
		def usedFields = contactInstance?.customFields ?: []
		def usedFieldNames = []
		usedFields.each() { field ->
			usedFieldNames.add(field.name)
		}
		def allFields = CustomField.getAllUniquelyNamed()
		def unusedFields = []
		allFields.each() {
			if(!usedFieldNames.contains(it))
				unusedFields.add(it)
		}
		def contactGroupInstanceList = contactInstance?.groups ?: []
		if(params.contactId && !contactInstance) {
			flash.message = message(code:'contact.not.found')
			redirect action:'show'
			return false
		} else if(params.groupId && !contactList.contactsSection) {
			flash.message = message(code:'group.not.found')
			redirect action:'show'
			return false
		} else if(params.smartGroupId && !contactList.contactsSection) {
			flash.message = message(code:'smartgroup.not.found')
			redirect action:'show'
			return false
		}

		[contactInstance: contactInstance,
				checkedContactList: ',',
				contactInstanceList: contactInstanceList,
				contactInstanceTotal: contactInstanceTotal,
				contactsSection: contactList.contactsSection,
				contactsSectionContactTotal: contactList.contactsSectionContactTotal,
				contactFieldInstanceList: usedFields,
				contactGroupInstanceList: contactGroupInstanceList,
				contactGroupInstanceTotal: contactGroupInstanceList.size(),
				nonContactGroupInstanceList: contactInstance ? Group.findAllWithoutMember(contactInstance) : null,
				uniqueFieldInstanceList: unusedFields,
				fieldInstanceList: CustomField.findAll(),
				groupInstanceList: Group.findAll(),
				smartGroupInstanceList: SmartGroup.list()]
	}

	def createContact() {
		render view:'show', model: [contactInstance: new Contact(params),
				contactFieldInstanceList: [],
				contactGroupInstanceList: [],
				contactGroupInstanceTotal: 0,
				nonContactGroupInstanceList: Group.findAll(),
				uniqueFieldInstanceList: CustomField.getAllUniquelyNamed(),
				fieldInstanceList: CustomField.findAll(),
				groupInstanceList: Group.findAll(),
				smartGroupInstanceList: SmartGroup.list()] << contactSearchService.contactList(params)
	}

	def saveContact() {
		def contactInstance = Contact.get(params.contactId) ?: new Contact()
		contactInstance.properties = params
		def saveSuccessful = false
		if(attemptSave(contactInstance)) {
			parseContactFields(contactInstance)
			saveSuccessful = attemptSave(contactInstance)
		}
		if(request.xhr) {
			def data = [success:saveSuccessful,
					flagCSSClasses: contactInstance.flagCSSClasses,
					contactPrettyPhoneNumber: contactInstance.mobile?.toPrettyPhoneNumber() ] << getContactErrors(contactInstance)
			render (data as JSON)
		} else {
			redirect action:'show', params:[contactId:contactInstance.id]
		}
	}

	private getContactErrors(contactInstance) {
		contactInstance.validate()
		def data = [errors:[:]]
		contactInstance.errors.allErrors.each {
			def field =  it.field
			def errorMessage = g.message(error:it)
			if(data.errors."$field") {
				data.errors."$field" << errorMessage
			} else {
				data.errors."$field" = [errorMessage]
			}
		}
		log.info "##### ${data}"
		return data
	}

	def update() {
		withContact { contactInstance ->
			contactInstance.properties = params
			parseContactFields(contactInstance)
			attemptSave(contactInstance)
			if(params.groupId) {
				redirect(action:'show', controller:params.contactsSection, id: params.groupId, params:[contactId: contactInstance.id, sort:params.sort, offset:params.offset])
			} else {
				redirect(action:'show', params:[contactId:contactInstance.id, offset:params.offset], max:params.max)
			}
		}
	}

	def updateMultipleContacts() {
		params.remove("mobile") //TODO remove on refactor of contact form
		getCheckedContacts().each { c ->
			parseContactFields(c)
			attemptSave(c)
		}
		flash.message = message(code:'default.updated.multiple', args:[message(code:'contact.label')])
		render view:'show', model:show()
	}

	def confirmDelete() {
		def contactInstanceList = getCheckedContacts()
		[contactInstanceList:contactInstanceList,
				contactInstanceTotal:contactInstanceList.size()]
	}

	def delete() {
		// FIXME looks like someone doesn't know what's going wrong here and clutching at straws
		Contact.withTransaction { status ->
			getCheckedContacts()*.delete()
		}
		flash.message = message(code:'default.deleted', args:[message(code:'contact.label')])
		redirect action:'show'
	}

	def newCustomField() {
		def contactInstance = params.contactId
		def customFieldInstance = new CustomField()
		customFieldInstance.properties = params
		[customFieldInstance: customFieldInstance,
				contactInstance: contactInstance]
	}

	def search() {
		render(template:'search_results', model:contactSearchService.contactList(params))
	}

	def checkForDuplicates() {
		def foundContact = Contact.findByMobile(params.mobile)
		if (foundContact && foundContact.id.toString() == params.contactId) {
			render true
		} else {
			render (!foundContact && params.mobile)
		}
	}

//> PRIVATE HELPER METHODS
	private def attemptSave(contactInstance) {
		def mobile = params.mobile?.replaceAll(/\D/, '')
		if(params.mobile && params.mobile[0] == '+') mobile = '+' + mobile
		def existingContact = mobile ? Contact.findByMobileLike(mobile) : null
		if (existingContact && existingContact != contactInstance) {
			flash.message = "${message(code: 'contact.exists.warn')}  " + g.link(action:'show', params:[contactId:Contact.findByMobileLike(params.mobile)?.id], g.message(code: 'contact.view.duplicate'))
			return false
		}
		if(contactInstance.save()) {
			def redirectParams = [contactId: contactInstance.id]
			if(params.groupId) redirectParams << [groupId: params.groupId]
			return true
		}
		return false
	}

	def multipleContactGroupList() {
		def groups = Group.getGroupLists(getCheckedContactIds())
		render(view: '_multiple_contact', model: [sharedGroupInstanceList:groups.shared,
				nonSharedGroupInstanceList:groups.nonShared])
	}

	private def withContact = withDomainObject Contact, { params.contactId }

	private def getCheckedContacts() {
		Contact.getAll(getCheckedContactIds()) - null
	}

	private def getCheckedContactIds() {
		def ids = params['contact-select']?:
				params.checkedContactList? params.checkedContactList?.tokenize(',')?.unique():
				[params.contactId]
		return ids.flatten().unique()
	}

	private def parseContactFields(Contact contactInstance) {
		updateCustomFields(contactInstance)
		updateGroups(contactInstance)
		return contactInstance.stripNumberFields()
	}

	private def updateGroups(Contact contactInstance) {
		def groupsToAdd = params.groupsToAdd?.tokenize(',')?.unique()
		def groupsToRemove = params.groupsToRemove?.tokenize(',')

		// Check for errors in groupsToAdd and groupsToRemove
		if(!groupsToAdd?.disjoint(groupsToRemove)) {
			contactInstance.errors.reject(message(code: 'contact.addtogroup.error'))
			return false
		}

		groupsToRemove.each() { id ->
			contactInstance.removeFromGroups(Group.get(id))
		}
		groupsToAdd.each() { id ->
			contactInstance.addToGroups(Group.get(id))
		}
		return contactInstance
	}

	private def updateCustomFields(Contact contactInstance) {
		def fieldsToAdd = params.fieldsToAdd?.tokenize(',')
		def fieldsToRemove = params.fieldsToRemove?.tokenize(',')
		def existingFields = CustomField.findAllByContact(contactInstance)

		fieldsToAdd?.each { name ->
			def fieldsByName = params["newCustomField-$name"]
			if(fieldsByName && !(name in existingFields*.name)) {
				contactInstance.addToCustomFields(new CustomField(name:name, value:fieldsByName))
			}
		}

		fieldsToRemove?.each { name ->
			def toRemove = CustomField.findByContactAndName(contactInstance, name)
			if(toRemove) {
				contactInstance.removeFromCustomFields(toRemove)
				toRemove.delete()
			}
		}

		//also save any existing fields that have changed
		existingFields.each { f ->
			def newValue = params["customField-$f.id"]
			if(newValue && f.value != newValue) {
				f.value = newValue
				f.save()
			}
		}
		return contactInstance
	}
}

