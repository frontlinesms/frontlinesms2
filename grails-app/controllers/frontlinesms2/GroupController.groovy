package frontlinesms2

class GroupController {
	static allowedMethods = [update: "POST"]

	def list = {
		['groups' : Group.list()]
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
		redirect(controller: "message", action: "inbox")
	}

	def show = {
		redirect(controller: "contact", action: "list", params:[groupId : params.id])
	}
}