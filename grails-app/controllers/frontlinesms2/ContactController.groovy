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

    def show = {
        def contactInstance = Contact.get(params.id) // TODO withContact {}
        if (!contactInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'contact.label', default: 'Contact'), params.id])}"
            redirect(action: "list")
        } else {
			def contactGroupInstanceList = contactInstance.groups;
			
//			def csvContactGroupsIds = ','
//			contactGroupInstanceList.each() {
//				csvContactGroupsIds += it.id + ','
//			}

			def nonContactGroupInstanceList = Group.findAllWithoutMember(contactInstance)
//			println "contactGroupInstanceList: ${contactGroupInstanceList}"
//			println "nonContactGroupInstanceList: ${nonContactGroupInstanceList}"
//			println "intersection: ${contactGroupInstanceList.intersect(nonContactGroupInstanceList)}"
//			println "intersection: ${nonContactGroupInstanceList.intersect(contactGroupInstanceList)}"

			return [contactInstance:contactInstance,
                                contactGroupInstanceList: contactGroupInstanceList,
								contactGroupInstanceTotal: contactGroupInstanceList.size(),
                                nonContactGroupInstanceList: Group.findAllWithoutMember(contactInstance) //,
//                                contactGroupInstanceListString: csvContactGroupsIds
							] << list()
        }
    }

	def update = {
        def contactInstance = Contact.get(params.id) // TODO replace with withContact closure
        if (contactInstance) {
            if (params.version) { // TODO create withVersionCheck closure for use in all Controllers
                def version = params.version.toLong()
                if (contactInstance.version > version) {

                    contactInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'contact.label', default: 'Contact')] as Object[], "Another user has updated this Contact while you were editing")
                    render(view: "edit", model: [contactInstance: contactInstance])
                    return
                }
            }
//			params.groups = params.groups.tokenize(',').collect() { Group.get(Long.parseLong(it)) }
//			println params.groups

            contactInstance.properties = params
			println "groups after properties set: ${contactInstance.groups}"

            if (!contactInstance.hasErrors() && contactInstance.save(flush: true)) {
				println "groups before add: ${contactInstance.groups}"
				params.groupsToAdd.tokenize(',').each() {
					def g = Group.get(Long.parseLong(it))
					println "Adding to group: ${g}"
					contactInstance.addToGroups(g, true)
				}
				println "groups before remove: ${contactInstance.groups}"
				params.groupsToRemove.tokenize(',').each() {
					def g = Group.get(Long.parseLong(it))
					println "Removing from group: ${g}"
					contactInstance.removeFromGroups(g, true)
				}
				println "groups after changes: ${contactInstance.groups}"

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
		return [contactInstance:contactInstance]
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

//	def removeGroup = {
//		def groupInstance = Group.get(params.id)
//		groupInstance.removeFromMembers()
//
//	}

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
