package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class PollControllerSpec extends ControllerSpec {

	def "default action is CREATE"() {
		when:
			controller.index()
		then:
			controller.redirectArgs.controller == 'poll' || !controller.redirectArgs.controller
			controller.redirectArgs.action == 'create'
	}

//	def "should fetch starred pending messages"() {
//		setup:
//			registerMetaClass(Fmessage)
//			def starredPendingMessages = [new Fmessage(starred: true)]
//			Fmessage.metaClass.'static'.getPendingMessages = {isStarred->
//				if(isStarred)
//					return starredPendingMessages
//			}
//			mockParams.starred = true
//			mockDomain(Folder)
//			mockDomain(Poll)
//		when:
//			def results = controller.pending()
//		then:
//			results['messageInstanceList'] == starredPendingMessages
//	}
}

