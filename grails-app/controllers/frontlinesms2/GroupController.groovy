package frontlinesms2

class GroupController {
	static allowedMethods = [update: "POST"]

	def list = {
		['groups' : Group.list()]
	}

	def update = {
		def group = Group.get(params['id'])
		group.properties = params
		if(group.save(failOnError: true, flush: true))
			flash['message'] = "Group saved successfully"
		
	}
}