package frontlinesms2

import grails.converters.JSON

class FolderController extends ControllerUtils {
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

	def trashService
	
	def index() {
		 redirect(action: "create", params: params)
	}

	def create() {
		def folderInstance = new Folder()
		folderInstance.properties = params
		[folderInstance: folderInstance]
	}

	def rename() {
		def folderInstance = Folder.get(params.ownerId)
		[folderInstance: folderInstance]
	}

	def save() {
		def folderInstance = new Folder(params)
		if (folderInstance.save(flush:true)) {
			flashMessage = message(code: 'folder.create.success')

			withFormat {
				json {
					render([ok:true] as JSON)
				}
			}
		} else {
			flashMessage = message(code: 'folder.create.failed')
			withFormat {
				json {
					render([ok:false, text:message(code: folderInstance.errors.allErrors[0].codes[7])] as JSON)
				}
			}
		}
	}

	def update() {
		def folderInstance = Folder.get(params.id)
		folderInstance.name = params.name
		if (folderInstance.save(flush:true)) {
			flashMessage = message(code: 'folder.renamed')
			
			withFormat {
				json {
					render([ok:true] as JSON)
				}
			}
		} else {
			withFormat {
				json {
					render([ok:false, text:message(code: folderInstance.errors.allErrors[0].codes[7])] as JSON)
				}
			}
		}
	}

	def archive() {
		withFolder { folder ->
			folder.archive()
			if(folder.save()) {
				flashMessage = defaultMessage 'archived'
			} else {
				flashMessage = defaultMessage 'archive.failed'
			}
			redirect controller:"message", action:"inbox"
		}
	}
	
	def unarchive() {
		withFolder { folder ->
			folder.unarchive()
			if(folder.save()) {
				flashMessage = defaultMessage 'unarchived'
			} else {
				flashMessage = defaultMessage 'unarchive.failed'
			}
			redirect controller:"archive", action:"folderList"
		}
	}

	def confirmDelete() {
		def folderInstance = Folder.get(params.id)
		render view: "../activity/confirmDelete", model: [ownerInstance: folderInstance]
	}
	
	def delete() {
		withFolder { folder ->
			trashService.sendToTrash(folder)
			flashMessage = defaultMessage 'trashed'
			redirect controller:"message", action:"inbox"
		}
	}
	
	def restore() {
		withFolder { folder ->
			if(trashService.restore(folder)) {
				flashMessage = defaultMessage 'restored'
			} else {
				flashMessage = defaultMessage 'restore.failed', folder.id
			}
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

