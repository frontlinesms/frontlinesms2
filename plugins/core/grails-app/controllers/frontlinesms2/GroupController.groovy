package frontlinesms2

class GroupController {
	static allowedMethods = [update: "POST"]

	def list = {
		[groups:Group.list()]
	}

	def update = {
		def group = Group.get(params.id.toLong())
		group.properties = params
		if(group.save(flush:true)) {
			flash.message = message(code:'group.update.success')
			redirect controller:"contact", action:"show", params:[groupId:params.id]
		} else {
			flash.message = message(code:'group.save.fail')
			redirect controller:"contact", action:"show", params:params
		}
	}

	def show = {
		params.groupId = params.id
		redirect controller:"contact", action:"show", params:params
	}
	
	def create = {
		def groupInstance = new Group()
		groupInstance.properties = params
		[groupInstance: groupInstance]
	}
	
	def save = {
		def groupInstance = new Group(params)
		if (groupInstance.save(flush:true)) {
			flash.message = message(code:'default.created.message', args:[message(code:'group.label'), groupInstance.name])
		} else {
			flash.message = message(code:'group.save.fail')
		}
		redirect controller:"contact", params:[flashMessage:flash.message]
	}
	
	def rename = {}

	def confirmDelete = {
		[groupName: Group.get(params.groupId)?.name]
	}
	
	def delete = {
		if (Group.get(params.id)?.delete(flush:true))
			flash.message = message(code:'default.deleted.message', args:[message(code:'group.label')])
		else
			flash.message = message(code:'group.delete.fail')
		redirect controller:"contact"
	}
}

