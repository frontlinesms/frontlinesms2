package frontlinesms2

class TrashService {
    	def emptyTrash() {
    	TextMessage.findAllByIsDeleted(true).each {
    		def conn = it.receivedOn
    		if(conn) {
    			conn.removeFromMessages(it)
    			conn.save()
    		}
    	}
		TextMessage.findAllByIsDeleted(true)*.delete()
		MessageOwner.findAllByDeleted(true)*.delete()
		Trash.findAll()*.delete()
    	}
    
	def sendToTrash(object) {
		log.info "Deleting ${object}"
		if (object instanceof frontlinesms2.Interaction) {
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

	boolean restore(object) {
		Trash.findByObject(object)?.delete()
		object.restoreFromTrash()
		return object.save() as boolean
	}
}

