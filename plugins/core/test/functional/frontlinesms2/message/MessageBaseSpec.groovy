package frontlinesms2.message

import frontlinesms2.*

class MessageBaseSpec extends grails.plugin.geb.GebSpec {
	
	static createTestMessages() {
		Fmessage.build(src:'Bob', text:'hi Bob')
		Fmessage.build(src:'Alice', text:'hi Alice')
		Fmessage.build(src:'+254778899', text:'test')
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
		Fmessage.build(src:'Alex', text:'meeting at 11.00')
		Fmessage.build(src:'Bob', text:'hi Bob')
		Fmessage.build(src:'Michael', text:'Can we get meet in 5 minutes')

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
	
	static createTestContacts() {
		Contact.build(name:'Alice', mobile:'+254778899')
		Contact.build(name:'Bob', mobile:'+254987654')
	}
	
	static createTestData() {
		Contact.build(name:'Bob', mobile:'+254987654')
		
		Fmessage.build(src:'Bob',text:'hi Bob')
		Fmessage.build(src:'Alice', text:'hi Alice')
		Fmessage.build(src:'+254778899', text:'test')
		Fmessage.build(src:'Mary', text:'hi Mary')
		Fmessage.build(src:'+254445566', text:'test')
		
		def chickenMessage = Fmessage.build(src:'Barnabus', text:'i like chicken')
		def liverMessage = Fmessage.build(src:'Minime', text:'i like liver')

		def poll = new Poll(name:'Miauow Mix')
		[chicken:chickenMessage, liver:liverMessage, Unknown:null].each { value, m ->
			PollResponse r = new PollResponse(value:value)
			poll.addToResponses(r)
			if(m) r.addToMessages(m)
		}
		poll.save(flush:true, failOnError:true)

		def message1 = Fmessage.build(src:'Cheney', text:'i hate chicken')
		def message2 = Fmessage.build(src:'Bush', text:'i hate liver')
		def fools = Folder.build(name:'Fools')
		fools.addToMessages(message1)
		fools.addToMessages(message2)
		fools.save(failOnError:true, flush:true)
	}
}

