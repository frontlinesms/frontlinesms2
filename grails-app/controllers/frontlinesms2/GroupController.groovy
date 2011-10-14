package frontlinesms2

class GroupController {
	static allowedMethods = [update: "POST"]

	def list = {
		['groups' : Group.list()]
	}
	
	def rename = {
	}

	def update = {
		def group = Group.get(params['id'])
		group.properties = params
		if(group.validate()){
			group.save(failOnError: true, flush: true)
			flash['message'] = "Group updated successfully"
		}
		else
			flash['message'] = "Group not saved successfully"
		redirect(controller: "contact", action: "show", params:[groupId : params.id])
	}

	def show = {
		redirect(controller: "contact", action: "show", params:[groupId : params.id])
	}
	
	def create = {
		def groupInstance = new Group()
		groupInstance.properties = params
		[groupInstance: groupInstance]
	}
	
	def save = {
		def groupInstance = new Group(params)
		if (!groupInstance.hasErrors() && groupInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.updated.message', args: [message(code: 'contact.label', default: 'Group'), groupInstance.id])}"
		} else {
			flash.message = "error"
		}
		redirect(controller: "contact", params:[flashMessage: flash.message])
	}
}