package frontlinesms.core

import frontlinesms2.*

class AutoreplyService {

    def saveInstance(Announcement announcement, params) {
		announcement.name = params.name ?: announcement.name
		announcement.sentMessageText = params.messageText ?: announcement.sentMessageText
		announcement.save(failOnError:true,flush:true)
		return announcement
	}
}
