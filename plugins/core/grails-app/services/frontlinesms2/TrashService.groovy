package frontlinesms2

class TrashService {
    def emptyTrash() {
		Fmessage.findAllByIsDeleted(true)*.delete()
		MessageOwner.findAllByDeleted(true)*.delete()
		Trash.findAll()*.delete()
    }
    
    static sendToTrash(object) {
		if (object instanceof frontlinesms2.Fmessage) {
			object.isDeleted = true
			new Trash(displayName: object.displayName, displayText: object.text, objectClass: object.class, objectId: object.id).save()
			object.save()
		} else if (object instanceof frontlinesms2.MessageOwner) {
			object.deleted = true
			object.messages.each {
				it.isDeleted = true
				it.save()
			}
			def detail = "x messages" //message(code: 'fmessage.count.many', args: [object.messages.size()])
			new Trash(displayName: object.name, displayText: detail, objectClass: object.class, objectId: object.id).save()
			object.save(failOnError: true, flush: true)
		}
	}
}
