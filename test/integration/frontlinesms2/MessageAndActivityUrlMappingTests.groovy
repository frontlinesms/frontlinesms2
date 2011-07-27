package frontlinesms2

class MessageAndActivityUrlMappingTests extends grails.test.GrailsUrlMappingsTestCase {
	def testInboxView() {
		assertForwardUrlMapping('/message/inbox/show/123', controller:'message', action:'inbox') {
			messageId = 123
		}
	}
	
	def testSentView() {
		assertForwardUrlMapping('/message/sent/show/123', controller:'message', action:'sent') {
			messageId = 123
		}
	}

	def testPendingView() {
		assertForwardUrlMapping('/message/pending/show/123', controller:'message', action:'pending') {
			messageId = 123
		}
	}

	def testRadioShowView() {
		assertForwardUrlMapping('/message/radioShow/123', controller:'message', action:'radioShow') {
			ownerId = 123
		}

	}

	def testTrashView() {
		assertForwardUrlMapping('/message/trash/show/123', controller:'message', action:'trash') {
			messageId = 123
		}
	}

	def testPollMessageView() {
		assertForwardUrlMapping('/message/poll/123/show/456', controller:'message', action:'poll') {
			messageId = 456
			ownerId = 123
		}
	}

	def testRadioMessageView() {
		assertForwardUrlMapping('/message/radioShow/123/show/3', controller:'message', action:'radioShow') {
			ownerId = 123
			messageId = 3
		}
	}

	def testFolderMessageView() {
		assertForwardUrlMapping('/message/folder/123/show/456', controller:'message', action:'folder') {
			messageId = 456
			ownerId = 123
		}
	}
}

