package frontlinesms2

class FolderController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
	def index = {
		 redirect(action: "create", params: params)
	}

	def create = {
		def folderInstance = new Folder()
		folderInstance.properties = params
		[folderInstance: folderInstance]
	}
	
	def save = {
		def folderInstance = new Folder(params)
		if (folderInstance.save(flush: true)) {
			flash.message = "${message(code: 'default.created.message', args: [message(code: 'folder.label', default: 'Folder'), folderInstance.id])}"
			redirect(controller: "message", action:'inbox', params:[flashMessage: flash.message])
		} else {
			flash.message = "error"
			redirect(controller: "message", action:'inbox', params:[flashMessage: flash.message])
		}
	}
	
	def archive = {
		withFolder { folder ->
			folder.archive()
			folder.save(flush:true, failOnError:true)
		
			flash.message = "Folder was archived successfully!"
			redirect(controller: "message", action: "inbox")
		}
	}
	
	def unarchive = {
		withFolder { folder ->
			folder.unarchive()
			folder.save()
		}

		flash.message = "Folder was unarchived successfully!"
		redirect(controller: "archive", action: "folderList")
	}
	
	def confirmDelete = {
		def folderInstance = Folder.get(params.id)
		render view: "../message/confirmDelete", model: [ownerInstance: folderInstance]
	}
	
	def delete = {
		withFolder { folder ->
			folder.deleted = true
			new Trash(identifier:folder.name, message:"${folder.liveMessageCount}", objectType:folder.class.name, linkId:folder.id).save(failOnError: true, flush: true)
			folder.save(failOnError: true, flush: true)
		}
		flash.message = "Folder has been trashed!"
		redirect(controller:"message", action:"inbox")
	}
	
	def restore = {
		withFolder { folder ->
			folder.deleted = false
			folder.save(failOnError: true, flush: true)
			Trash.findByLinkId(folder.id)?.delete()
		}
		flash.message = "Folder has been restored!"
		redirect(controller: "message", action: "trash")
	}

	private def withFolder(Closure c) {
		def folderInstance = Folder.get(params.id)
		if (folderInstance) c folderInstance
		else render(text: "Could not find folder with id ${params.id}") // TODO handle error state properly
	}
}

