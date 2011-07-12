package frontlinesms2

import grails.plugin.spock.*

class MessageControllerSpec extends ControllerSpec {

	def setup() {
		mockDomain Fmessage
		registerMetaClass(Fmessage)
		Fmessage.metaClass.'static'.countAllMessages = {isStarred -> [inbox:0,pending:0,deleted:0,sent:0]}
		mockParams.messageText = "text"
		controller.messageSendService = new MessageSendService()
		def sahara = new Group(name: "Sahara", members: [new Contact(primaryMobile: "12345"),new Contact(primaryMobile: "56484")])
		def thar = new Group(name: "Thar", members: [new Contact(primaryMobile: "12121"), new Contact(primaryMobile: "22222")])
		mockDomain Group, [sahara, thar]
		mockConfig('''
			pagination.max = 5
		''')

	}

	def "should send message to all the members in a group"() {
		setup:
			mockParams.groups = "Sahara"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.list()*.dst.containsAll(["12345","56484"])
	}

	def "should send message to all the members in multiple groups"() {
		setup:
			mockParams.groups = ["Sahara", "Thar"]
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.list()*.dst.containsAll(["12345","56484","12121","22222"])
	}
	
	def "should send a message to the given address"() {
		setup:
			mockParams.addresses = "+919544426000"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.count() == 1
	}

	def "should eliminate duplicate address if present"() {
		setup:
			mockParams.addresses = "12345"
			mockParams.groups = "Sahara"
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.count() == 2
	}

	def "should send message to each recipient in the list of address"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			mockParams.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			Fmessage.list()*.dst.containsAll(addresses)
			Fmessage.count() == 3
	}
	
	def "should display flash message on successful message sending"() {
		setup:
			def addresses = ["+919544426000", "+919004030030", "+1312456344"]
			mockParams.addresses = addresses
		when:
			assert Fmessage.count() == 0
			controller.send()
		then:
			controller.flash.message == "Message has been queued to send to +919544426000, +919004030030, +1312456344"
			
	}

//	def "should fetch pending messages"() {
//		def isStarred = false
//		expect:
//			setupDataAndAssert(isStarred, {fmessage, max, offset ->
//
//				Fmessage.metaClass.'static'.getPendingMessages = {starred->
//					assert starred == isStarred
//					assert max == grails
//					assert offset == isStarred
//					[fmessage]
//				}
//				controller.pending()
//			})
//	}

	def "should fetch starred inbox messages"() {
		def isStarred = true
		expect:
		setupDataAndAssert(isStarred, 5, 1, {fmessage ->
			Fmessage.metaClass.'static'.getInboxMessages = {starred, max, offset ->
				assert starred == isStarred
				assert max == mockParams.max
				assert offset == mockParams.offset
				[fmessage]
			}
			controller.inbox()
		})
	}




	def "should fetch all inbox messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, null, null, {fmessage ->
				Fmessage.metaClass.'static'.getInboxMessages = {starred, max, offset ->
					assert isStarred == starred
					assert max == 5
					assert offset == 0
					[fmessage]
				}
				controller.inbox()
		})
	}


	def "should fetch starred pending messages"() {
		def isStarred = true
		expect:
			setupDataAndAssert(isStarred, 3, 4, {fmessage ->
				Fmessage.metaClass.'static'.getPendingMessages = {starred, max, offset ->
					assert starred == isStarred
					assert max == mockParams.max
					assert offset == mockParams.offset
					return [fmessage]
				}
				controller.pending()
		})
	}

	def "should fetch all pending messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, null, null, {fmessage ->
				Fmessage.metaClass.'static'.getPendingMessages = {starred, max, offset ->
					assert isStarred == starred
					assert max == 5
					assert offset == 0
					return [fmessage]
				}
				controller.pending()
		})
	}

	def "should fetch all poll messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, null, null, {fmessage ->
				def poll = new Poll(id: 2L, responses: [new PollResponse()])
				mockParams.ownerId = 2L
				mockDomain Poll, [poll]
				poll.metaClass.getMessages = {starred, max, offset ->
					assert starred == isStarred
					assert max == 5
					assert offset == 0
					[fmessage]
				}
				 controller.poll()
				
		})
	}

//	//FIXME: Need to  replace it with 'setupDataAndAssert' method.
//	def "should fetch starred poll messages"() {
//		setup:
//			registerMetaClass(Poll)
//			def starredFmessage = new Fmessage(starred: true)
//			def unstarredFmessage = new Fmessage(starred: false)
//			def poll = new Poll(id: 2L, responses: [new PollResponse()])
//			mockParams.starred = true
//			mockParams.ownerId = 2L
//			mockDomain Folder
//			mockDomain Poll, [poll]
//			mockDomain Fmessage, [starredFmessage, unstarredFmessage]
//			poll.metaClass.getMessages = {isStarred->
//				isStarred ? [starredFmessage] : [starredFmessage, unstarredFmessage]
//			}
//		when:
//			def results = controller.poll()
//		then:
//			results['messageInstanceList'] == [starredFmessage]
//	}
//
	def "should fetch all folder messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, null, null, {fmessage ->
				def folder = new Folder(id: 2L, messages: [fmessage])
				mockParams.ownerId = 2L
				mockDomain Folder, [folder]
				folder.metaClass.getFolderMessages = {starred, max, offset->
						assert starred == isStarred
						assert max == 5
						assert offset == 0
						[fmessage]
				}
				controller.folder()
			})
	}

	def "should fetch starred folder messages"() {
		expect:
			def isStarred = true
			setupDataAndAssert (isStarred, 3, 2, {fmessage ->
				def folder = new Folder(id: 2L, messages: [fmessage])
				mockParams.ownerId = 2L
				mockDomain Folder, [folder]
				folder.metaClass.getFolderMessages = {starred, max, offset->
					assert starred == isStarred
					assert max == mockParams.max
					assert offset == mockParams.offset
					[fmessage]
				}
				controller.folder()
			})
	}




	def "should fetch starred trash messages"() {
		expect:
			def isStarred = true
			setupDataAndAssert (isStarred, 3, 4, {fmessage ->
				Fmessage.metaClass.'static'.getDeletedMessages = {starred, max, offset->
					assert starred == starred
					assert max == mockParams.max
					assert offset == mockParams.offset
					[fmessage]
				}
				controller.trash()
			})
	}

	def "should fetch all trash messages"() {
		expect:
			def isStarred = false
			setupDataAndAssert (isStarred, null, null, {fmessage ->
				Fmessage.metaClass.'static'.getDeletedMessages = {starred, max, offset ->
					assert starred == isStarred
					assert max == 5
					assert offset == 0
					[fmessage]
				}
				controller.trash()
			})
	}

	def "should show the starred sent messages"() {
		expect:
			def isStarred = true
			setupDataAndAssert (isStarred, 3, 4, {fmessage ->
				Fmessage.metaClass.'static'.getSentMessages = {starred, max, offset ->
					assert starred == isStarred
					assert max == 3
					assert offset == 4
					[fmessage]
				}
				controller.sent()
			})
	}

	def "should show all the  sent messages"() {
		expect:
			def isStarred = false
			setupDataAndAssert (isStarred,null, null, {fmessage ->
				Fmessage.metaClass.'static'.getSentMessages = {starred, max, offset ->
					assert starred == isStarred
					assert max == 5
					assert offset == 0
					[fmessage]
				}

				controller.sent()
			})
	}

    def setupDataAndAssert = {isStarred, max, offset, closure  ->
		setup:
			registerMetaClass(Fmessage)
			def fmessage = new Fmessage(src: "src1", starred: isStarred)
			mockDomain Folder
			mockDomain Poll
			mockDomain Contact
			mockParams.starred = isStarred
			mockParams.max = max
			mockParams.offset = offset
		when:
			def results = closure.call(fmessage)
		then:
			results['messageInstanceList'] == [fmessage]
			results['messageInstanceTotal'] == 1
			results['messageInstance'] == fmessage
			results['messageInstanceList']*.contactExists == [false]
			results['messageInstanceList']*.displaySrc == ["src1"]
    }
}