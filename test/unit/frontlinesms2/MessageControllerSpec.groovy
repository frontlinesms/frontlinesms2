package frontlinesms2

import grails.plugin.spock.*
import frontlinesms2.enums.MessageStatus

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
		setup:
			registerMetaClass(Fmessage)
			def pendingMessages = [new Fmessage(src: "src"), new Fmessage(src: "src")]
			Fmessage.metaClass.'static'.getPendingMessages = {isStarred->
				pendingMessages
			}
			mockDomain(Folder)
			mockDomain(Poll)
			mockDomain(Contact)
		when:
			def results = controller.pending()
		then:
			results['messageInstanceList'] == pendingMessages
			results['messageSection'] == 'pending'
			results['messageInstanceTotal'] == 2
			results['messageInstance'] == pendingMessages[0]
			results['messageInstanceList']*.contactExists == [false, false]
			results['messageInstanceList']*.displaySrc == ["src", "src"]
	}

	def "should fetch starred inbox messages"() {
		setup:
			registerMetaClass(Fmessage)
			def starredInboxMessages = [new Fmessage(starred: true)]
			mockParams.starred = true
			mockDomain(Folder)
			mockDomain(Poll)
			Fmessage.metaClass.'static'.getInboxMessages = {isStarred ->
				if(isStarred)
					return starredInboxMessages
			}
		when:
			def results = controller.inbox()
		then:
			results['messageInstanceList'] == starredInboxMessages
	}


	def "should fetch all inbox messages"() {
		setup:
			registerMetaClass(Fmessage)
			def isStarred = false
			def fmessageList = [new Fmessage(starred: isStarred)]
			mockParams.starred = isStarred
			mockDomain(Folder)
			mockDomain(Poll)
			Fmessage.metaClass.'static'.getInboxMessages = {starred ->
				assert isStarred == starred
				return fmessageList
			}
		when:
			def results = controller.inbox()
		then:
			results['messageInstanceList'] == fmessageList
	}


	def "should fetch starred pending messages"() {
		setup:
			registerMetaClass(Fmessage)
			def isStarred = true
			def starredPendingMessages = [new Fmessage(starred: isStarred)]
			mockParams.starred = isStarred
			mockDomain(Folder)
			mockDomain(Poll)
			Fmessage.metaClass.'static'.getPendingMessages = {starred ->
				assert starred == isStarred
				return starredPendingMessages
			}
		when:
			def results = controller.pending()
		then:
			results['messageInstanceList'] == starredPendingMessages
	}

	def "should fetch all pending messages"() {
		setup:
			registerMetaClass(Fmessage)
			def isStarred = false
			def fmessages = [new Fmessage(starred: isStarred)]
			mockParams.starred = isStarred
			mockDomain(Folder)
			mockDomain(Poll)
			Fmessage.metaClass.'static'.getPendingMessages = {starred ->
				assert isStarred == starred
				return fmessages
			}
		when:
			def results = controller.pending()
		then:
			results['messageInstanceList'] == fmessages
	}

	def "should fetch all poll messages"() {
		setup:
			registerMetaClass(Poll)
			def isStarred = false
			def fmessage = new Fmessage(starred: isStarred)
			def poll = new Poll(id: 2L, responses: [new PollResponse()])
			mockParams.starred = isStarred
			mockParams.ownerId = 2L
			mockDomain Folder
			mockDomain Poll, [poll]
			mockDomain Fmessage
			poll.metaClass.getMessages = {starred->
				assert starred == isStarred
				[fmessage]
			}
		when:
			def results = controller.poll()
		then:
			results['messageInstanceList'] == [fmessage]
	}

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
		setup:
			def starredFmessage = new Fmessage(starred: true)
			def unstarredFmessage = new Fmessage(starred: false)
			def folder = new Folder(id: 2L, messages: [starredFmessage, unstarredFmessage])
			def isStarred = false

			mockParams.starred = isStarred
			mockParams.ownerId = 2L
			mockDomain Folder, [folder]
			mockDomain Poll
			mockDomain Fmessage

			folder.metaClass.getFolderMessages = {starred->
					assert starred == isStarred
					[starredFmessage, unstarredFmessage]
			}
		when:
			def results = controller.folder()
		then:
			results['messageInstanceList'] == [starredFmessage, unstarredFmessage]
	}

	def "should fetch starred folder messages"() {
		setup:
			def isStarred = true
			def starredFmessage = new Fmessage(starred: true)
			def unstarredFmessage = new Fmessage(starred: false)
			def folder = new Folder(id: 2L, messages: [starredFmessage, unstarredFmessage])
			mockParams.starred = isStarred
			mockParams.ownerId = 2L
			mockDomain Folder, [folder]
			mockDomain Poll
			mockDomain Fmessage
			folder.metaClass.getFolderMessages = {starred->
					assert starred == isStarred
					[starredFmessage]
			}
		when:
			def results = controller.folder()
		then:
			results['messageInstanceList'] == [starredFmessage]
	}

	def "should fetch starred trash messages"() {
		setup:
			registerMetaClass(Fmessage)
			def fmessage = new Fmessage(deleted: true, starred: true)
			mockParams.starred = true
			mockDomain Folder
			mockDomain Poll
			Fmessage.metaClass.'static'.getDeletedMessages = {isStarred->
				if(isStarred)
					[fmessage]
			}
		when:
			def results = controller.trash()
		then:
			results['messageInstanceList'] == [fmessage]
	}

	def "should fetch all trash messages"() {
		setup:
			registerMetaClass(Fmessage)
			def isStarred = false
			def fmessage = new Fmessage(deleted: true, starred: isStarred)
			mockParams.starred = isStarred
			mockDomain Folder
			mockDomain Poll
			Fmessage.metaClass.'static'.getDeletedMessages = {starred->
				assert starred == isStarred
				[fmessage]
			}
		when:
			def results = controller.trash()
		then:
			results['messageInstanceList'] == [fmessage]
	}

	def "should show the starred sent messages"() {
		setup:
			registerMetaClass(Fmessage)
			def isStarred = true
			def fmessage = new Fmessage(status: MessageStatus.SENT, starred: isStarred)
			Fmessage.metaClass.'static'.getSentMessages = {starred ->
				assert starred == isStarred
				[fmessage]
			}
			mockDomain Folder
			mockDomain Poll
			mockParams.starred = isStarred
		when:
			def results = controller.sent()
		then:
			results['messageInstanceList'] == [fmessage]
			results['messageSection'] == 'sent'
			results['messageInstanceTotal'] == 1
			results['messageInstance'] == fmessage
	}

	def "should show all the  sent messages"() {
		setup:
			registerMetaClass(Fmessage)
			def isStarred = false
			def fmessage = new Fmessage(status: MessageStatus.SENT, starred: isStarred)
			Fmessage.metaClass.'static'.getSentMessages = {starred ->
				assert starred == isStarred
				[fmessage]
			}
			mockDomain Folder
			mockDomain Poll
			mockParams.starred = isStarred
		when:
			def results = controller.sent()
		then:
			results['messageInstanceList'] == [fmessage]
			results['messageSection'] == 'sent'
			results['messageInstanceTotal'] == 1
			results['messageInstance'] == fmessage
	}

}