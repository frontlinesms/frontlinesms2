package frontlinesms2

class ContactController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def index = {
		redirect action: "list", params:params
	}

	def list = {
		def model = buildList()

		params.contactId = params.contactId?:model.contactInstanceList[0]?.id
		if(params.contactId) {
			redirect(action:'show', params:params)
		} else {
			model
		}
	}

	def buildList = {
		def groupInstance = params.groupId? Group.findById(params.groupId): null
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		params.sort = "name"

		def contactInstanceList, contactInstanceTotal
		if(groupInstance) {
			contactInstanceList = groupInstance.members as List
			contactInstanceTotal = groupInstance.members.size()
		} else {
			contactInstanceList = Contact.list(params)
			contactInstanceTotal = Contact.count()
		}
		if(params.flashMessage) {
			flash.message = params.flashMessage
		}
		[contactInstanceList: contactInstanceList,
				contactInstanceTotal: contactInstanceTotal,
				fieldInstanceList: CustomField.findAll(),
				groupInstanceList: Group.findAll(),
				groupInstanceTotal: Group.count(),
				contactsSection: groupInstance]
	}

	def show = {
		params.sort = "name"
		withContact { contactInstance ->
			def contactGroupInstanceList = contactInstance.groups?: []
			def contactFieldInstanceList = contactInstance.customFields
			[contactInstance:contactInstance,
					contactFieldInstanceList: contactFieldInstanceList,
					contactGroupInstanceList: contactGroupInstanceList,
					contactGroupInstanceTotal: contactGroupInstanceList.size(),
					nonContactGroupInstanceList: Group.findAllWithoutMember(contactInstance),
					uniqueFieldInstanceList: CustomField.getAllUniquelyNamed()] << buildList()
		}
	}

	def update = {
		withContact { contactInstance ->
			if (params.version) { // TODO create withVersionCheck closure for use in all Controllers
				def version = params.version.toLong()
				if (contactInstance.version > version) {
					contactInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'contact.label', default: 'Contact')] as Object[], "Another user has updated this Contact while you were editing")
					render(view: "edit", model: [contactInstance: contactInstance])
					return
				}
			}
			contactInstance.properties = params
			updateData(contactInstance)
			render(view:'show', model:show()<<[contactInstance:contactInstance])
		}
	}

	def createContact = {
		def model = [contactInstance:new Contact(params),
					contactFieldInstanceList: [],
					contactGroupInstanceList: [],
					contactGroupInstanceTotal: 0,
					nonContactGroupInstanceList: Group.findAll(),
					uniqueFieldInstanceList: CustomField.getAllUniquelyNamed()] << buildList()

		render(view:'show', model:model)
	}

	def createGroup = {
		def groupInstance = new Group()
		groupInstance.properties = params
		[groupInstance: groupInstance] << buildList()
	}

	def saveContact = {
		def contactInstance = new Contact(params)
		contactInstance.properties = params

		updateData(contactInstance)

		flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
		redirect(action:'createContact')
	}

	def saveGroup = {
		def groupInstance = new Group(params)
		if (!groupInstance.hasErrors() && groupInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Group'), groupInstance.id])}"
			redirect(controller:'group', action:'show', id: groupInstance.id, params: [flashMessage: flash.message])
		} else {
			flash.message = "error"
			redirect(controller:'contact', action:'list', params: [flashMessage: flash.message, contactId: params.contactId])
		}
	}

	def newCustomField = {
		def contactInstance = params.contactId
		def customFieldInstance = new CustomField()
		customFieldInstance.properties = params
		[customFieldInstance: customFieldInstance,
				contactInstance: contactInstance]
	}

	private def withContact(Closure c) {
		def contactInstance = Contact.get(params.contactId)
		if (contactInstance) {
			c contactInstance
		} else {
			flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
			redirect(action: "list")
		}
	}

	private def updateData(Contact contactInstance) {
		// Check for errors in groupsToAdd and groupsToRemove
		def groupsToAdd = params.groupsToAdd.tokenize(',').unique()
		def groupsToRemove = params.groupsToRemove.tokenize(',')
		def fieldsToAdd = params.fieldsToAdd.tokenize(',')
		def fieldsToRemove = params.fieldsToRemove.tokenize(',')
		if(!groupsToAdd.disjoint(groupsToRemove)) {
			contactInstance.errors.reject('Cannot add and remove from the same group!')
		} else if (!contactInstance.hasErrors() && contactInstance.save(flush: true)) {
			groupsToAdd.each() {
				contactInstance.addToGroups(Group.get(it))
			}
			groupsToRemove.each() {
				contactInstance.removeFromGroups(Group.get(it))
			}

			fieldsToAdd.each() { name ->
				def existingFields = CustomField.findAllByNameAndContact(name, contactInstance)
				def fieldsByName = params."$name"
				println name
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

			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
			def redirectParams = [contactId:contactInstance.id]
			if(params.groupId) redirectParams << [groupId: params.groupId]
			return
		}
	}
}
