package frontlinesms2

class TrashService {

    def emptyTrash() {
		Fmessage.findAllByDeleted(true)*.delete()
		Poll.findAllByDeleted(true)*.delete()
		Folder.findAllByDeleted(true)*.delete()
		Trash.findAll()*.delete()
    }
}