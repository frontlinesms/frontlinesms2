package frontlinesms2

class FolderController {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def trashService
	
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
		if (folderInstance.save(flush:true)) {
			flash.message = defaultMessage 'created'
			redirect controller:"message", action:'folder', id:folderInstance.id
		} else {
			flash.message = defaultMessage 'create.failed'
			redirect controller:"message", action:'inbox', params:[flashMessage:flash.message]
		}
	}

	def archive = {
		withFolder { folder ->
			folder.archive()
			if(folder.save()) {
				flash.message = defaultMessage 'archived'
			} else {
				flash.message = defaultMessage 'archive.failed'
			}
			redirect controller:"message", action:"inbox"
		}
	}
	
	def unarchive = {
		withFolder { folder ->
			folder.unarchive()
			if(folder.save()) {
				flash.message = defaultMessage 'unarchived'
			} else {
				flash.message = defaultMessage 'unarchive.failed'
			}
			redirect controller:"archive", action:"folderList"
		}
	}

	def confirmDelete = {
		def folderInstance = Folder.get(params.id)
		render view: "../activity/confirmDelete", model: [ownerInstance: folderInstance]
	}
	
	def delete = {
		withFolder { folder ->
			trashService.sendToTrash(folder)
			flash.message = defaultMessage 'trashed'
			redirect controller:"message", action:"inbox"
		}
	}
	
	def restore = {
		withFolder { folder ->
			folder.deleted = false
			folder.save(failOnError:true, flush:true)
			Trash.findByObject(folder)?.delete()
			flash.message = defaultMessage 'restored'
			redirect controller:"message", action:"trash"
		}
	}

	private def withFolder(Closure c) {
		def folderInstance = Folder.get(params.id)
		if (folderInstance) c folderInstance
		else render text:defaultMessage('notfound', params.id)
	}

	private def defaultMessage(String code, Object... args=[]) {
		def folderName = message code:'folder.label'
		return message(code:'default.' + code,
				args:[folderName] + args)
	}
}

