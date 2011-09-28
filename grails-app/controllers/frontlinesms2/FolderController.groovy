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
			folder.archive(true)
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
		redirect(controller: "archive", action: "folderView")
	}

	private def withFolder(Closure c) {
		def folderInstance = Folder.get(params.id)
		if (folderInstance) c folderInstance
		else render(text: "Could not find folder with id ${params.id}") // TODO handle error state properly
	}
}

