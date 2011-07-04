package frontlinesms2

import grails.plugin.spock.*

class MessageControllerSpec extends ControllerSpec {

	def setup() {
		mockDomain Fmessage
		mockParams.messageText = "text"
		controller.messageSendService = new MessageSendService()
		def sahara = new Group(name: "Sahara", members: [new Contact(address: "12345"),new Contact(address: "56484")])
		def thar = new Group(name: "Thar", members: [new Contact(address: "12121"), new Contact(address: "22222")])
		mockDomain Group, [sahara, thar]
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

	def "should fetch pending messages"() {
		def isStarred = false
		expect:
			withSetup(isStarred, {fmessage ->

				Fmessage.metaClass.'static'.getPendingMessages = {starred->
					assert starred == isStarred 
					[fmessage]
				}
				controller.pending()
			})
	}

	def "should fetch starred inbox messages"() {
		def isStarred = true
		expect:
			withSetup(isStarred, {fmessage ->
				Fmessage.metaClass.'static'.getInboxMessages = {starred ->
					assert starred == isStarred
					[fmessage]
				}
				controller.inbox()
			})
	}


	def "should fetch all inbox messages"() {
		def isStarred = false
		expect:
			withSetup(isStarred, {fmessage ->
				Fmessage.metaClass.'static'.getInboxMessages = {starred ->
					assert isStarred == starred
					[fmessage]
				}
				controller.inbox()
		})
	}


	def "should fetch starred pending messages"() {
		def isStarred = true
		expect:
			withSetup(isStarred, {fmessage ->
				Fmessage.metaClass.'static'.getPendingMessages = {starred ->
					assert starred == isStarred
					return [fmessage]
				}
				controller.pending()
		})
	}

	def "should fetch all pending messages"() {
		def isStarred = false
		expect:
			withSetup(isStarred, {fmessage ->
				Fmessage.metaClass.'static'.getPendingMessages = {starred ->
					assert isStarred == starred
					return [fmessage]
				}
				controller.pending()
		})
	}

	def "should fetch all poll messages"() {
		def isStarred = false
		expect:
			withSetup(isStarred, {fmessage ->
				def poll = new Poll(id: 2L, responses: [new PollResponse()])
				mockParams.ownerId = 2L
				mockDomain Poll, [poll]
				poll.metaClass.getMessages = {starred->
					assert starred == isStarred
					[fmessage]
				}
				 controller.poll()
		})
	}

	//FIXME: Need to  replace it with 'withSetup' method.
	def "should fetch starred poll messages"() {
		setup:
			registerMetaClass(Poll)
			def starredFmessage = new Fmessage(starred: true)
			def unstarredFmessage = new Fmessage(starred: false)
			def poll = new Poll(id: 2L, responses: [new PollResponse()])
			mockParams.starred = true
			mockParams.ownerId = 2L
			mockDomain Folder
			mockDomain Poll, [poll]
			mockDomain Fmessage, [starredFmessage, unstarredFmessage]
			poll.metaClass.getMessages = {isStarred->
				isStarred ? [starredFmessage] : [starredFmessage, unstarredFmessage]
			}
		when:
			def results = controller.poll()
		then:
			results['messageInstanceList'] == [starredFmessage]
	}

	def "should fetch all folder messages"() {
		def isStarred = false
		expect:
			withSetup(isStarred, {fmessage ->
				def folder = new Folder(id: 2L, messages: [fmessage])
				mockParams.ownerId = 2L
				mockDomain Folder, [folder]
				folder.metaClass.getFolderMessages = {starred->
						assert starred == isStarred
						[fmessage]
				}
				controller.folder()
			})
	}

	def "should fetch starred folder messages"() {
		expect:
			def isStarred = true
			withSetup (isStarred, {fmessage ->
				def folder = new Folder(id: 2L, messages: [fmessage])
				mockParams.ownerId = 2L
				mockDomain Folder, [folder]
				folder.metaClass.getFolderMessages = {starred->
					assert starred == isStarred
					[fmessage]
				}
				controller.folder()
			})
	}




	def "should fetch starred trash messages"() {
		expect:
			def isStarred = true
			withSetup (isStarred, {fmessage ->
				Fmessage.metaClass.'static'.getDeletedMessages = {starred->
					assert starred == starred
					[fmessage]
				}
				controller.trash()
			})
	}

	def "should fetch all trash messages"() {
		expect:
			def isStarred = false
			withSetup (isStarred, {fmessage ->
				Fmessage.metaClass.'static'.getDeletedMessages = {starred->
					assert starred == isStarred
					[fmessage]
				}
				controller.trash()	
			})
	}

	def "should show the starred sent messages"() {
		expect:
			def isStarred = true
			withSetup (isStarred, {fmessage ->
				Fmessage.metaClass.'static'.getSentMessages = {starred ->
					assert starred == isStarred
					[fmessage]
				}
				controller.sent()
			})
	}

	def "should show all the  sent messages"() {
		expect:       
			def isStarred = false
			withSetup (isStarred, {fmessage ->
				Fmessage.metaClass.'static'.getSentMessages = {starred ->
					assert starred == isStarred
					[fmessage]
				}

				controller.sent()
			})
	}

    def withSetup = {isStarred, closure  ->
		setup:
			registerMetaClass(Fmessage)
			def fmessage = new Fmessage(src: "src1", starred: isStarred)
			mockDomain Folder
			mockDomain Poll
			mockDomain Contact
			mockParams.starred = isStarred
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