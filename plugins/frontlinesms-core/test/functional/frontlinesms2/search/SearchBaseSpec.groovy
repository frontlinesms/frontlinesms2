package frontlinesms2.search

import frontlinesms2.*

class SearchBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestMessages() {
		remote {
			TextMessage.build(src:'Bob', text:'hi Bob')
			TextMessage.build(src:'Alice', text:'hi Alice')
			TextMessage.build(src:'+254778899', text:'test')
			null
		}
	}

	static createTestGroups() {
		remote {
			Group.build(name:'Listeners')
			Group.build(name:'Friends')
			null
		}
	}

	static createTestMessages2() {
		remote {
			TextMessage.build(src:'Doe', text:'meeting at 11.00', date:new Date()-1)
			TextMessage.build(src:'Alex', text:'hi alex', date:new Date()-1, starred:true)
			null
		}
	}

	private static createMiaouwMixPoll() {
		remote {
			Poll p = new Poll(name:'Miauow Mix')

			def chickenResponse = new PollResponse(key:'A', value:'chicken')
			p.addToResponses(chickenResponse)
			p.addToResponses(key:'B', value:'liver')
			p.addToResponses(PollResponse.createUnknown())
			p.save(failOnError:true, flush:true)

			chickenResponse.addToMessages(TextMessage.build(src:'Joe', text:'eat more cow'))
			p.save(failOnError:true, flush:true)
			null
		}
	}

	static createTestPollsAndFolders() {
		createMiaouwMixPoll()

		remote {
			Folder.build(name:"Work")
			null
		}
	}

	static createTestContactsAndCustomFieldsAndMessages() {
		remote {
			Contact.build(name:'Alex', mobile:'+254987654')
					.addToCustomFields(name:'town', value:'Paris')
					.save(failOnError:true, flush:true)
			Contact.build(name:'Mark', mobile:'+254333222')
					.addToCustomFields(name:'like', value:'cake')
					.addToCustomFields(name:'ik', value:'car')
					.save(failOnError:true, flush:true)
			Contact.build(name:"Toto", mobile:'+666666666')
					.addToCustomFields(name:'like', value:'ake')
					.save(failOnError:true, flush:true)

			TextMessage.build(src:'+666666666', text:'finaly i stay in bed')
			null
		}
	}

	static createInboxTestMessages() {
		remote {
			TextMessage.build(src:'Bob', text:'hi Bob', date:new Date()-2)
			TextMessage.build(src:'Alice', text:'hi Alice', date:new Date()-1, starred:true)
			null
		}

		frontlinesms2.message.MessageBaseSpec.createMiaouwMixPoll()
	}

	static createSearchTestMessages() {
		remote {
			TextMessage.build(src:'Alex', text:'meeting at 11.00', date:new Date()-1)
			TextMessage.build(src:'Bob', text:'hi Bob', date:new Date()-1)
			TextMessage.build(src:'Michael', text:'Can we get meet in 5 minutes')
			null
		}

		frontlinesms2.message.MessageBaseSpec.createMiaouwMixPoll()
	}

	static createTestContacts() {
		remote {
			Contact.build(name:'Alice', mobile:'+254778899')
			Contact.build(name:'Bob', mobile:'+254987654')
			null
		}
	}
}

