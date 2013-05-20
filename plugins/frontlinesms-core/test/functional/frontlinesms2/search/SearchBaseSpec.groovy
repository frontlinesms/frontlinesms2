package frontlinesms2.search

import frontlinesms2.*

class SearchBaseSpec extends grails.plugin.geb.GebSpec {
	static createTestMessages() {
		remote {
			Fmessage.build(src:'Bob', text:'hi Bob')
			Fmessage.build(src:'Alice', text:'hi Alice')
			Fmessage.build(src:'+254778899', text:'test')
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
			Fmessage.build(src:'Doe', text:'meeting at 11.00', date:new Date()-1)
			Fmessage.build(src:'Alex', text:'hi alex', date:new Date()-1, starred:true)
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

			chickenResponse.addToMessages(Fmessage.build(src:'Joe', text:'eat more cow'))
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

			Fmessage.build(src:'+666666666', text:'finaly i stay in bed')
			null
		}
	}

	static createInboxTestMessages() {
		remote {
			Fmessage.build(src:'Bob', text:'hi Bob', date:new Date()-2)
			Fmessage.build(src:'Alice', text:'hi Alice', date:new Date()-1, starred:true)

			frontlinesms2.message.MessageBaseSpec.createMiaouwMixPoll()
			null
		}
	}

	static createSearchTestMessages() {
		remote {
			Fmessage.build(src:'Alex', text:'meeting at 11.00', date:new Date()-1)
			Fmessage.build(src:'Bob', text:'hi Bob', date:new Date()-1)
			Fmessage.build(src:'Michael', text:'Can we get meet in 5 minutes')

			frontlinesms2.message.MessageBaseSpec.createMiaouwMixPoll()
			null
		}
	}

	static createTestContacts() {
		remote {
			Contact.build(name:'Alice', mobile:'+254778899')
			Contact.build(name:'Bob', mobile:'+254987654')
			null
		}
	}
}

