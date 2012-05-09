package frontlinesms2.search

import frontlinesms2.*

class SearchBaseSpec extends grails.plugin.geb.GebSpec {
	
	static createTestMessages() {
		Fmessage.build(src:'Bob', text:'hi Bob')
		Fmessage.build(src:'Alice', text:'hi Alice')
		Fmessage.build(src:'+254778899', text:'test')
	}
	
	static createTestGroups() {
		Group.build(name:'Listeners')
		Group.build(name:'Friends')
	}
	
	static createTestMessages2() {
		Fmessage.build(src:'Doe', text:'meeting at 11.00', date:new Date()-1)
		Fmessage.build(src:'Alex', text:'hi alex', date:new Date()-1)
	}
	
	static createTestPollsAndFolders() {
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		def unknownResponse = new PollResponse(value:'Unknown')

		Poll p = new Poll(name:'Miauow Mix')
		[chickenResponse, liverResponse, unknownResponse].each { p.addToResponses(it) }
		chickenResponse.addToMessages(Fmessage.build(src:'Joe', text:'eat more cow'))
		p.save(failOnError:true, flush:true)

		Folder.build(name:"Work")
	}
	
	static createTestContactsAndCustomFieldsAndMessages() {
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
	}
	
	static createInboxTestMessages() {
		Fmessage.build(src:'Bob', text:'hi Bob', date:new Date()-2)
		Fmessage.build(src:'Alice', text:'hi Alice', date:new Date()-1, starred:true)

		def chickenMessage = Fmessage.build(src:'Barnabus', text:'i like chicken')
		def liverMessage = Fmessage.build(src:'Minime', text:'i like liver')

		def poll = new Poll(name:'Miauow Mix')
		[chicken:chickenMessage, liver:liverMessage, Unknown:null].each { value, m ->
			PollResponse r = new PollResponse(value:value)
			poll.addToResponses(r)
			if(m) r.addToMessages(m)
		}
		poll.save(flush:true, failOnError:true)
	}
	
	static createSearchTestMessages() {
		Fmessage.build(src:'Alex', text:'meeting at 11.00', date:new Date()-1)
		Fmessage.build(src:'Bob', text:'hi Bob', date:new Date()-1)
		Fmessage.build(src:'Michael', text:'Can we get meet in 5 minutes')

		def chickenMessage = Fmessage.build(src:'Barnabus', text:'i like chicken')
		def liverMessage = Fmessage.build(src:'Minime', text:'i like liver')
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		def unknownResponse = new PollResponse(value:'Unknown')

		Poll p = new Poll(name:'Miauow Mix')
		[chickenResponse, liverResponse, unknownResponse].each { p.addToResponses(it) }
		p.save(failOnError:true, flush:true)

		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
	}
	
	static createTestContacts() {
		Contact.build(name:'Alice', mobile:'+254778899')
		Contact.build(name:'Bob', mobile:'+254987654')
	}
}

