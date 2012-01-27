package frontlinesms2

class TrashService {

    def emptyTrash() {
		MessageOwner.findAllByDeleted(true)*.delete()
		Fmessage.findAllByIsDeleted(true)*.delete()
		Trash.findAll()*.delete()
    }
}