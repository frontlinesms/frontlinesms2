package frontlinesms2

class ContactController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    
    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
        def groupInstance = params.id? Group.findById(params.id): null
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
		params.sort = "name"

        def contactInstanceList, contactInstanceTotal
        if(groupInstance) {
                contactInstanceList = groupInstance.members
                contactInstanceTotal = groupInstance.members.size()
        } else {
                contactInstanceList = Contact.list(params)
                contactInstanceTotal = Contact.count()
        }

        [contactInstanceList: contactInstanceList,
                contactInstanceTotal: contactInstanceTotal,
                groupInstanceList: Group.findAll(),
                groupInstanceTotal: Group.count(),
                contactsSection: groupInstance]
    }

	def show = {
		params.sort = "name"
		withContact { contactInstance ->
			def contactGroupInstanceList = contactInstance.groups

			[contactInstance:contactInstance,
					contactGroupInstanceList: contactGroupInstanceList,
					contactGroupInstanceTotal: contactGroupInstanceList.size(),
					nonContactGroupInstanceList: Group.findAllWithoutMember(contactInstance)] << list()
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
			
			// Check for errors in groupsToAdd and groupsToRemove
			def groupsToAdd = params.groupsToAdd.tokenize(',').unique()
			def groupsToRemove = params.groupsToRemove.tokenize(',')
			if(!groupsToAdd.disjoint(groupsToRemove)) {
				contactInstance.errors.reject('Cannot add and remove from the same group!')
				render(view: "edit", model: [contactInstance: contactInstance])
			} else if (!contactInstance.hasErrors() && contactInstance.save(flush: true)) {
				groupsToAdd.each() {
					contactInstance.addToGroups(Group.get(it), true)
				}
				groupsToRemove.each() {
					contactInstance.removeFromGroups(Group.get(it), true)
				}

				flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
				redirect(action: "show", id: contactInstance.id)
			} else {
				render(view: "edit", model: [contactInstance: contactInstance])
			}
		}
	}

    def createContact = {
        def contactInstance = new Contact()
        contactInstance.properties = params
        [contactInstance: contactInstance] << list()
    }

	def createGroup = {
        def groupInstance = new Group()
        groupInstance.properties = params
        [groupInstance: groupInstance] << list()
    }

    def saveContact = {
        def contactInstance = new Contact(params)
		if (contactInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
            redirect(action: "list", id: contactInstance.id)
        }
        else {
            render(view: "createContact", model: [contactInstance: contactInstance])
        }
    }

    def saveGroup = {
        def groupInstance = new Group(params)
		if (groupInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'contact.label', default: 'Group'), groupInstance.id])}"
            redirect(action: "list", id: groupInstance.id)
        }
        else {
            render(view: "createGroup", model: [groupInstance: groupInstance])
        }
    }

    def delete = {
		withContact {
            try {
                contactInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
                redirect(action: "list")
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
    }

	def withContact(Closure c) {
		println "params: ${params}"
	    def contactInstance = Contact.get(params.id)
	    if (contactInstance) {
			c contactInstance
        } else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
            redirect(action: "list")
        }
	}
}
