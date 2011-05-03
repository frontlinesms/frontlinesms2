package frontlinesms2

class ContactController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index = {
        redirect(action: "list", params: params)
    }

    def list = {
		def groupInstance = params.id? Group.findById(params.id): null
		def groupInstanceTotal
        params.max = Math.min(params.max ? params.int('max') : 10, 100)

		def contactInstanceList, contactInstanceTotal
		println "Contacts: ${Contact.list(params)}"
		if(groupInstance) {
			contactInstanceList = groupInstance.members
			contactInstanceTotal = groupInstance.members.size()
		} else {
			contactInstanceList = Contact.list(params)
			contactInstanceTotal = Contact.count()
		}

         return [contactInstanceList: contactInstanceList,
					contactInstanceTotal: contactInstanceTotal,
					groupInstanceList: Group.findAll(),
				 	groupInstanceTotal: Group.count(),
					contactsSection: groupInstance]
    }

    def createContact = {
        def contactInstance = new Contact()
        contactInstance.properties = params
        return [contactInstance: contactInstance] << list()
    }

	def createGroup = {
        def groupInstance = new Group()
        groupInstance.properties = params
        return [groupInstance: groupInstance] << list()
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

    def show = {
        def contactInstance = Contact.get(params.id)
        if (!contactInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [contactInstance: contactInstance] << list()
        }
    }

    def edit = {
        def contactInstance = Contact.get(params.id)
        if (!contactInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [contactInstance: contactInstance]
        }
    }

    def update = {
        def contactInstance = Contact.get(params.id)
        if (contactInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (contactInstance.version > version) {
                    
                    contactInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'contact.label', default: 'Contact')] as Object[], "Another user has updated this Contact while you were editing")
                    render(view: "edit", model: [contactInstance: contactInstance])
                    return
                }
            }
            contactInstance.properties = params
            if (!contactInstance.hasErrors() && contactInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Contact'), contactInstance.id])}"
                redirect(action: "show", id: contactInstance.id)
            }
            else {
                render(view: "edit", model: [contactInstance: contactInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete = {
        def contactInstance = Contact.get(params.id)
        if (contactInstance) {
            try {
                contactInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
            redirect(action: "list")
        }
    }
}
