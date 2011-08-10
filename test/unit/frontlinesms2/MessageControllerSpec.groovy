package frontlinesms2

import grails.plugin.spock.*

class MessageControllerSpec extends ControllerSpec {
	def setup() {
		mockDomain Contact
		mockDomain Fmessage
		registerMetaClass(Fmessage)
		registerMetaClass(Contact)
		Fmessage.metaClass.'static'.countAllMessages = {isStarred -> [inbox:0,pending:0,deleted:0,sent:0]}
		Contact.metaClass.'static'.withNewSession = {closure -> closure.call()}
		mockParams.messageText = "text"
		mockParams.max = 10
		mockParams.offset = 0
		mockParams.starred = false
	    controller.messageSendService = new MessageSendService()
	 
		def sahara = new Group(name: "Sahara", members: [new Contact(primaryMobile: "12345"),new Contact(primaryMobile: "56484")])
		def thar = new Group(name: "Thar", members: [new Contact(primaryMobile: "12121"), new Contact(primaryMobile: "22222")])
		mockDomain Group, [sahara, thar]
		mockConfig('''
			pagination.max = 10
		''')
	}


/*	def "should send message to all the members in a group"() {
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
			
	}*/

	def "should fetch starred inbox messages"() {
		def isStarred = true

		expect:
		setupDataAndAssert(isStarred, 5, 1, {fmessage ->
			Fmessage.metaClass.'static'.countInboxMessages = {params ->
				assert isStarred == params['starred']
				assert !params['archived']
				2
			}

			Fmessage.metaClass.'static'.getInboxMessages = { params ->
				if(params['starred'] && params['max'] == mockParams.max && params['offset'] == mockParams.offset && (!params['archived']))
					[fmessage]
			}
			controller.inbox()
		})
	}

	def "inbox should render archive layout for archived messages"() {
		def isStarred = true
		controller.params.archived = true
		def model
		expect:
			setupDataAndAssert(isStarred, 5, 1, {fmessage ->
				Fmessage.metaClass.'static'.countInboxMessages = {params ->
					assert isStarred == params['starred']
					assert params['archived']
					return 2
				}

				Fmessage.metaClass.'static'.getInboxMessages = { params ->
					if(params['starred'] && params['max'] == mockParams.max && params['offset'] == mockParams.offset && (params['archived']))
						[fmessage]
				}
				model = controller.inbox()
			})
		and:
			model.actionLayout == "archive"
	}

	def "sent should render archive layout for archived messages"() {
		def isStarred = true
		controller.params.archived = true
		def model
		expect:
			setupDataAndAssert(isStarred, 5, 1, {fmessage ->
				Fmessage.metaClass.'static'.countSentMessages = {params ->
					assert isStarred == params['starred']
					assert params['archived']
					return 2
				}

				Fmessage.metaClass.'static'.getSentMessages = { params ->
					if(params['starred'] && params['max'] == mockParams.max && params['offset'] == mockParams.offset && (params['archived']))
						[fmessage]
				}
				model = controller.sent()
			})
		and:
			model.actionLayout == "archive"
	}

	def "should fetch all inbox messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, null, null, { fmessage ->

				Fmessage.metaClass.'static'.getInboxMessages = { params ->
					[fmessage]
				}

				Fmessage.metaClass.'static'.countInboxMessages = {params ->
					assert isStarred == params['starred']
					assert !params['archived']
					2
				}
				controller.inbox()
		})
	}


	def "should fetch starred pending messages"() {
		def isStarred = true
		expect:
			setupDataAndAssert(isStarred, 3, 4, {fmessage ->
				Fmessage.metaClass.'static'.getPendingMessages = { params ->
					assert params['starred'] == isStarred
					assert params['max'] == mockParams.max
					assert params['offset'] == mockParams.offset
					return [fmessage]
				}

				Fmessage.metaClass.'static'.countPendingMessages = {starred ->
					assert isStarred == starred
					2
				}

				controller.pending()
		})
	}

	def "should fetch all pending messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, 10, 0, {fmessage ->
				Fmessage.metaClass.'static'.getPendingMessages = {params->
					assert params['starred'] == isStarred 
					assert params['max'] == 10
					assert params['offset'] == 0
					return [fmessage]
				}

				Fmessage.metaClass.'static'.countPendingMessages = {starred ->
					assert isStarred == starred
					2
				}

				controller.pending()
		})
	}

	def "should fetch all poll messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, 10, 0, {fmessage ->
				def poll = new Poll(id: 2L, responses: [new PollResponse()])
				mockParams.ownerId = 2L
				mockDomain Poll, [poll]
				poll.metaClass.getMessages = {params->
					assert params['starred'] == isStarred
					assert params['max'] == 10
					assert params['offset'] == 0
					[fmessage]
				}

				poll.metaClass.countMessages = {starred ->
					assert isStarred == starred
					2
				}
				controller.poll()

		})
	}

	def "should render list of polls in archive layout"() {
		def isStarred = false
		controller.params.archived = true
		def result
		expect:
			setupDataAndAssert(isStarred, 10, 0, {fmessage ->
				def poll = new Poll(id: 2L, responses: [new PollResponse()])
				mockParams.ownerId = 2L
				mockDomain Poll, [poll]
				poll.metaClass.getMessages = {params->
					assert params['starred'] == isStarred
					assert params['max'] == 10
					assert params['offset'] == 0
					[fmessage]
				}

				poll.metaClass.countMessages = {starred ->
					assert isStarred == starred
					2
				}
				result = controller.poll()

		})
		result['actionLayout'] == 'archive'
	}

	//FIXME: Need to  replace it with 'setupDataAndAssert' method.
	def "should fetch starred poll messages"() {
		setup:
			registerMetaClass(Poll)
			def starredFmessage = new Fmessage(starred: true)
			def unstarredFmessage = new Fmessage(starred: false)
			def poll = new Poll(id: 2L, responses: [new PollResponse()])
			mockParams.starred = true
			mockParams.ownerId = 2L
			mockParams.max = 2
			mockParams.offset =3
			mockDomain Folder
			mockDomain Poll, [poll]
			mockDomain RadioShow
			mockDomain Fmessage, [starredFmessage, unstarredFmessage]
			poll.metaClass.getMessages = {params->
				params['starred'] ? [starredFmessage] : [starredFmessage, unstarredFmessage]
			}

			poll.metaClass.countMessages = {isStarred ->
				2
			}

		when:
			def results = controller.poll()
		then:
			results['messageInstanceList'] == [starredFmessage]
	}

	def "should fetch all folder messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, null, null, {fmessage ->
				def folder = new Folder(id: 2L, messages: [fmessage])
				mockParams.ownerId = 2L
				mockDomain Folder, [folder]
				folder.metaClass.getFolderMessages = {params ->
						assert params['starred'] == isStarred
						[fmessage]
				}

				folder.metaClass.countMessages = {starred ->
					assert isStarred == starred
					2
				}
				controller.folder()
			})
	}

	def "should fetch all non-starred radio show messages"() {
		def isStarred = false
		expect:
			setupDataAndAssert(isStarred, null, null, {fmessage ->
				def radioShow = new RadioShow(id: 2L, messages: [fmessage])
				mockParams.ownerId = 2L
				mockDomain RadioShow, [radioShow]
				radioShow.metaClass.getShowMessages = {params ->
						assert params['starred'] == isStarred
						[fmessage]
				}

				radioShow.metaClass.countMessages = {starred ->
					assert isStarred == starred
					2
				}
				controller.radioShow()
			})
	}

	def "should fetch all starred radio show messages"() {
		def isStarred = true
		expect:
			setupDataAndAssert(isStarred, null, null, {fmessage ->
				def radioShow = new RadioShow(id: 2L, messages: [fmessage])
				mockParams.ownerId = 2L
				mockDomain RadioShow, [radioShow]
				radioShow.metaClass.getShowMessages = {params ->
						assert params['starred'] == isStarred
						[fmessage]
				}

				radioShow.metaClass.countMessages = {starred ->
					assert isStarred == starred
					2
				}
				controller.radioShow()
			})
	}

	def "should fetch starred folder messages"() {
		expect:
			def isStarred = true
			setupDataAndAssert (isStarred, 3, 2, {fmessage ->
				def folder = new Folder(id: 2L, messages: [fmessage])
				mockParams.ownerId = 2L
				mockDomain Folder, [folder]
				folder.metaClass.getFolderMessages = {params ->
					assert params['starred'] == isStarred
					assert params['max'] == mockParams.max
					assert params['offset'] == mockParams.offset
					[fmessage]
				}
				folder.metaClass.countMessages = {starred ->
					assert isStarred == starred
					2
				}
				controller.folder()
			})
	}


	def "should fetch starred trash messages"() {
		expect:
			def isStarred = true
			setupDataAndAssert (isStarred, 3, 4, {fmessage ->
				Fmessage.metaClass.'static'.getDeletedMessages = {params->
					assert params['starred'] == isStarred;
					assert params['max'] == mockParams.max;
					assert params['offset'] == mockParams.offset;
					[fmessage]
				}

				Fmessage.metaClass.'static'.countDeletedMessages = {starred ->
					assert isStarred == starred
					2
				}

				controller.trash()
			})
	}

	def "should fetch all trash messages"() {
		expect:
			def isStarred = false
			setupDataAndAssert (isStarred, null, null, {fmessage ->
				Fmessage.metaClass.'static'.getDeletedMessages = {params->
					assert params['starred'] == isStarred
					[fmessage]
				}

				Fmessage.metaClass.'static'.countDeletedMessages = {starred ->
					assert isStarred == starred
					2
				}
				
				controller.trash()
			})
	}

	def "should show the starred sent messages"() {
		expect:
			def isStarred = true
			setupDataAndAssert (isStarred, 3, 4, {fmessage ->
				Fmessage.metaClass.'static'.getSentMessages = {params->
					assert params['starred'] == isStarred
					assert params['max'] == 3
					assert params['offset'] == 4
					[fmessage]
				}

	 			Fmessage.metaClass.'static'.countSentMessages = {params ->
					assert isStarred == params['starred']
					assert !params['archived']
					2
				}

				controller.sent()
			})
	}

	def "should show all the  sent messages"() {
		expect:
			def isStarred = false
			setupDataAndAssert (isStarred,null, null, {fmessage ->
				Fmessage.metaClass.'static'.getSentMessages = {params->
					assert params['starred'] == isStarred
					[fmessage]
				}

				Fmessage.metaClass.'static'.countSentMessages = {params ->
					assert !params['archived']
					assert isStarred == params['starred']
					2
				}
				controller.sent()
			})
	}

     private void setupDataAndAssert(boolean isStarred, Integer max, Integer offset, Closure closure)  {
			registerMetaClass(Fmessage)
			def fmessage = new Fmessage(src: "src1", starred: isStarred)
			mockDomain Folder
			mockDomain Poll, [new Poll(archived: true), new Poll(archived: false)]
			mockDomain Contact
			mockDomain RadioShow 
			mockParams.starred = isStarred
			mockParams.max = max
			mockParams.offset = offset

			def results = closure.call(fmessage)

			assert results['messageInstanceList'] == [fmessage]
			assert results['messageInstanceTotal'] == 2
			assert results['messageInstance'] == fmessage
			assert results['messageInstanceList']*.contactExists == [false]
			assert results['messageInstanceList']*.contactExists == [false]
			assert results['pollInstanceList'].every {!it.archived}

    }
}