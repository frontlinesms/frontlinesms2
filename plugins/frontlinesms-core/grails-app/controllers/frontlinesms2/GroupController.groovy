package frontlinesms2

import grails.converters.JSON

class GroupController extends ControllerUtils {
	static allowedMethods = [update: "POST"]

	def groupService

	def list() {
		[groups:Group.list()]
	}

	def update() {
		def group = Group.get(params.id.toLong())
		group.properties = params
		if(group.save(flush:true)) {
			flash.message = message(code:'group.update.success')
			withFormat {
				json {
					render([ok:true] as JSON)
				}
			}
		} else {
			withFormat {
				json {
					render([ok:false, text:message(code: group.errors.allErrors[0].codes[7])] as JSON)
				}
			}
		}
	}

	def show() {
		params.groupId = params.id
		redirect controller:"contact", action:"show", params:params
	}
	
	def create() {
		def groupInstance = new Group()
		groupInstance.properties = params
		[groupInstance: groupInstance]
	}
	
	def save() {
		def groupInstance = new Group(params)
		if (groupInstance.save(flush:true)) {
			flash.message = message(code:'default.created.message', args:[message(code:'group.label'), groupInstance.name])
			withFormat {
				json {
					render([ok:true, id:groupInstance.id, name:groupInstance.name] as JSON)
				}
			}
		} else {
			withFormat {
				json {
					render([ok:false, text:message(code: groupInstance.errors.allErrors[0].codes[7])] as JSON)
				}
			}
		}
	}
	
	def rename() {}

	def confirmDelete() {
		[groupName: Group.get(params.groupId)?.name]
	}
	
	def delete() {
		if(groupService.delete(Group.get(params.id))) {
			flash.message = message(code:'default.deleted.message', args:[message(code:'group.label')])
		} else {
			flash.message = message(code:'group.delete.fail')
		}
		redirect controller:'contact', action: "show"
	}
}

