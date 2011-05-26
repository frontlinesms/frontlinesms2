package frontlinesms2

class MessageAndActivityUrlMappingTests extends grails.test.GrailsUrlMappingsTestCase {
	def testInboxView() {
		assertForwardUrlMapping('/message/inbox', controller:'message', action:'inbox') {
			id = null
		}
	}
	
	def testInboxMessageView() {
		assertForwardUrlMapping('/message/inbox/show/123', controller:'message', action:'show') {
			id = 123
			messageSection = 'inbox'
		}
	}
	
	def testSentView() {
		assertForwardUrlMapping('/message/sent', controller:'message', action:'sent') {
			id = null
		}
	}
	
	def testSentMessageView() {
		assertForwardUrlMapping('/message/sent/show/123', controller:'message', action:'show') {
			id = 123
			messageSection = 'sent'
		}
	}
	
	def testPollView() {
		assertForwardUrlMapping('/message/poll/123', controller:'message', action:'poll') {
			pollId = 123
		}
	}
	
	def testPollMessageView() {
		assertForwardUrlMapping('/message/poll/123/show/456', controller:'message', action:'show') {
			id = 456
			pollId = 123
			messageSection = 'poll'
		}
	}
}

