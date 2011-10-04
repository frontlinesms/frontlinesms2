package frontlinesms2

class TrashService {

    def trashPoll(Poll poll) {
    	poll.delete()
    }
    
    def trashFolder(Folder folder) {
        folder.delete()
    }
}
