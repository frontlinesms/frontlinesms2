package frontlinesms2

class GroupController {
	static allowedMethods = [update: "POST"]

	def list = {
		['groups' : Group.list()]
	}
	
	def update = {
		def group = Group.get(params.id.toLong())
		group.properties = params
		if(group.save(flush: true)) {
			flash.message = "Group updated successfully"
			redirect(controller: "contact", action: "show", params:[groupId : params.id])
		}
		else {
			flash.message = "Group not saved successfully"
			redirect(controller: "contact", action: "show", params:params)
		}
	}

	def show = {
		params.groupId = params.id
		redirect(controller: "contact", action: "show", params:params)
	}
	
	def create = {
		def groupInstance = new Group()
		groupInstance.properties = params
		[groupInstance: groupInstance]
	}
	
	def save = {
		def groupInstance = new Group(params)
		if (!groupInstance.hasErrors() && groupInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Group'), groupInstance.name])}"
		} else {
			flash.message = "error"
		}
		redirect(controller: "contact", params:[flashMessage: flash.message])
	}
	
	def rename = {
	}

	def confirmDelete = {
		[groupName: Group.get(params.groupId)?.name]
	}
	
	def delete = {
		if (Group.get(params.id)?.delete(flush: true))
			flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'group.label', default: 'Group'), ''])}"
		else
			flash.message = "unable to delete group"
		redirect(controller: "contact")
	}
}
