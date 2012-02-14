package frontlinesms2

class TrashService {
    def emptyTrash() {
		Fmessage.findAllByIsDeleted(true)*.delete()
		MessageOwner.findAllByDeleted(true)*.delete()
		Trash.findAll()*.delete()
    }
}