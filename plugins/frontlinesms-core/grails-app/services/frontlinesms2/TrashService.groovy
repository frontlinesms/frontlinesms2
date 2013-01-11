package frontlinesms2

class TrashService {
    	def emptyTrash() {
    	Fmessage.findAllByIsDeleted(true).each {
    		def conn = it.receivedOn
    		if(conn) {
    			conn.removeFromMessages(it)
    			conn.save()
    		}
    	}
		Fmessage.findAllByIsDeleted(true)*.delete()
		MessageOwner.findAllByDeleted(true)*.delete()
		Trash.findAll()*.delete()
    	}
    
	def sendToTrash(object) {
		println "Deleting ${object}"
		if (object instanceof frontlinesms2.Fmessage) {
			object.isDeleted = true
			new Trash(displayName:object.displayName,
					displayText:object.text.truncate(Trash.MAXIMUM_DISPLAY_TEXT_SIZE),
					objectClass:object.class.name,
					objectId:object.id).save()
			object.save(failOnError:true, flush:true)
		} else if (object instanceof frontlinesms2.MessageOwner) {
			object.deleted = true
			object.messages.each {
				it.isDeleted = true
				it.save(failOnError: true, flush: true)
			}
			def detail = object.messages?.size() + " message(s)"
			new Trash(displayName:object.name,
					displayText:detail,
					objectClass:object.class.name,
					objectId:object.id).save()
			object.save(failOnError:true, flush:true)
		}
	}

	def restore(object) {
		Trash.findByObject(object)?.delete()
		println "Restoring ${object}"
		object.restoreFromTrash()
		if (object.save())
			return true
		else
			return false
	}
}

