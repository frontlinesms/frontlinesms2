import spock.lang.*

import frontlinesms2.*

@TestFor(CoreUrlMappings)
@Mock([MessageController])
class MessageAndActivityUrlMappingSpec extends Specification {
	def testInboxView() {
		expect:
		assertForwardUrlMapping('/message/inbox/show/123', controller:'message', action:'inbox') {
			interactionId = 123
		}
	}
	
	def testSentView() {
		expect:
		assertForwardUrlMapping('/message/sent/show/123', controller:'message', action:'sent') {
			interactionId = 123
		}
	}

	def testPendingView() {
		expect:
		assertForwardUrlMapping('/message/pending/show/123', controller:'message', action:'pending') {
			interactionId = 123
		}
	}

	def testTrashView() {
		expect:
		assertForwardUrlMapping('/message/trash/show/123', controller:'message', action:'trash') {
			id = 123
		}
	}

	def testPollMessageView() {
		expect:
		assertForwardUrlMapping('/message/activity/123/show/456', controller:'message', action:'activity') {
			interactionId = 456
			ownerId = 123
		}
	}

	def testFolderMessageView() {
		expect:
		assertForwardUrlMapping('/message/folder/123/show/456', controller:'message', action:'folder') {
			interactionId = 456
			ownerId = 123
		}
	}
	
	def testAnnouncementMessageView() {
		expect:
		assertForwardUrlMapping('/message/activity/123/show/456', controller:'message', action:'activity') {
			interactionId = 456
			ownerId = 123
		}
	}
}

