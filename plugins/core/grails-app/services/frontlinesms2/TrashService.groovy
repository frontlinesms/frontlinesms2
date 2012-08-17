package frontlinesms2

class TrashService {
    	def emptyTrash() {
		Fmessage.findAllByIsDeleted(true)*.delete()
		MessageOwner.findAllByDeleted(true)*.delete()
		Trash.findAll()*.delete()
    	}
    
	def sendToTrash(object) {
		def trashDetails = object.sendToTrash()
		Trash.create(object, [displayText:trashDetails.text,
				displayName:trashDetails.displayName])
			.save()
		if(trashDetails.children instanceof Collection) {
			Trash.deleteForAll(trashDetails.children)
		}
		object.save(failOnError:true, flush:true)
	}

	def restore(object) {
		Trash.findByObject(object)?.delete()
		def children = object.restoreFromTrash()
		object.save(failOnError:true)
		if(children instanceof Collection) {
			children.each {
				restore(it)
				it.save(failOnError:true)
			}
		}
		return true
	}
}

