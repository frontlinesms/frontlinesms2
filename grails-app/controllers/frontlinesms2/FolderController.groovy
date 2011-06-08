package frontlinesms2

class FolderController {
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
			redirect(controller: "message")
		} else {
			println "Something went wrong while saving the Folder instance."
			folderInstance.errors.reject("Folder name must be defined")
			render(view: "create", model: [folderInstance: folderInstance])
		}
	}
}

