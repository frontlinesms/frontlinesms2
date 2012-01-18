package frontlinesms2

class TrashService {

    def emptyTrash() {
		Fmessage.findAllByIsDeleted(true)*.delete()
		Poll.findAllByDeleted(true)*.delete()
		Folder.findAllByDeleted(true)*.delete()
		Trash.findAll()*.delete()
    }
}