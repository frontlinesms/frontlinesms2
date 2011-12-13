package frontlinesms2

class NewMessagesService {
    static transactional = true

    def getNewMessageCount(params) {
		def section = params.messageSection
		Fmessage.section(false, false).count()
    }
}
