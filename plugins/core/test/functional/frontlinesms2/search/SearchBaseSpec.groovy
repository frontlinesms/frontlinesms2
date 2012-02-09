package frontlinesms2.search

import frontlinesms2.*

class SearchBaseSpec extends grails.plugin.geb.GebSpec {
	
	static createTestMessages() {
		[new Fmessage(src:'Bob', text:'hi Bob'),
				new Fmessage(src:'Alice', text:'hi Alice'),
				new Fmessage(src:'+254778899', text:'test')].each() {
					it.inbound = true
					it.date = new Date()
					it.save(failOnError:true)
				}
	}
	
	static createTestGroups() {
		new Group(name: 'Listeners').save(flush: true)
		new Group(name: 'Friends').save(flush: true)
	}
	
	static createTestMessages2() {
		[new Fmessage(src:'Doe', text:'meeting at 11.00', date: new Date()-1),
				new Fmessage(src:'Alex', text:'hi alex', date: new Date()-1)].each() {
			it.inbound = true
			it.save(failOnError:true)
		}
	}
	
	static createTestPollsAndFolders() {
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		new Fmessage(src:'Joe', text:'eat more cow', messageOwner:'chickenResponse', date: new Date(), inbound: true)
		Poll p = new Poll(name:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
		Folder f = new Folder(name: "Work").save(failOnError:true, flush:true)
		
	}
	
	static createTestContactsAndCustomFieldsAndMessages(){
		def firstContact = new Contact(name:'Alex', primaryMobile:'+254987654').save(failOnError:true)
		def secondContact = new Contact(name:'Mark', primaryMobile:'+254333222').save(failOnError:true)
		def thirdContact = new Contact(name:"Toto", primaryMobile:'+666666666').save(failOnError:true)
		
		[new CustomField(name:'town', value:'Paris', contact: firstContact),
			new CustomField(name:'like', value:'cake', contact: secondContact),
			new CustomField(name:'ik', value:'car', contact: secondContact),
			new CustomField(name:'like', value:'ake', contact: thirdContact),
			new Fmessage(src:'+666666666', text:'finaly i stay in bed', inbound:true, date: new Date())].each {
		it.save(failOnError:true)
		}
	}
	
	static createInboxTestMessages() {
		[new Fmessage(src:'Bob', text:'hi Bob', date: new Date() - 2),
				new Fmessage(src:'Alice', text:'hi Alice', date: new Date() - 1, starred: true)].each() {
					it.inbound = true
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', text:'i like chicken', inbound:true, date: new Date())
		def liverMessage = new Fmessage(src:'Minime', text:'i like liver', inbound: true, date: new Date())
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		def poll = new Poll(name:'Miauow Mix')
		poll.addToResponses(chickenResponse)
		poll.addToResponses(liverResponse)
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		
		poll.save(failOnError:true, flush:true)
	}
	
	static createSearchTestMessages() {
		[new Fmessage(src:'Alex', text:'meeting at 11.00', date: new Date()-1),
			new Fmessage(src:'Bob', text:'hi Bob', date: new Date()-1),
				new Fmessage(src:'Michael', text:'Can we get meet in 5 minutes', date: new Date())].each() {
					it.inbound = true
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', text:'i like chicken', inbound:true, date: new Date())
		def liverMessage = new Fmessage(src:'Minime', text:'i like liver', inbound: true, date: new Date())
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		Poll p = new Poll(name:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
	}
	
	static createTestContacts() {
		[new Contact(name: 'Alice', primaryMobile: '+254778899'),
			new Contact(name: 'Bob', primaryMobile: '+254987654')].each() { it.save(failOnError:true) }
	}
	
}

