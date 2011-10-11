package frontlinesms2.search

import frontlinesms2.*

class SearchBaseSpec extends grails.plugin.geb.GebSpec {
	
	static createTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test')].each() {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true)
				}
	}
	
	static createTestGroups() {
		new Group(name: 'Listeners').save(flush: true)
		new Group(name: 'Friends').save(flush: true)
	}
	
	static createTestMessages2() {
		[new Fmessage(src:'Doe', dst:'+254987654', text:'meeting at 11.00', dateReceived: new Date()-1),
				new Fmessage(src:'Alex', dst:'+254987654', text:'hi alex', dateReceived: new Date()-1)].each() {
			it.status = MessageStatus.INBOUND
			it.save(failOnError:true)
		}
	}
	
	static createTestPollsAndFolders() {
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		new Fmessage(src:'Joe', dst:'+254987654', text:'eat more cow', messageOwner:'chickenResponse')
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
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
			new Fmessage(src:'+666666666', dst:'+2549', text:'finaly i stay in bed', status:MessageStatus.INBOUND)].each {
		it.save(failOnError:true)
		}
	}
	
	static createInboxTestMessages() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date() - 2),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice', dateReceived: new Date() - 1, starred: true)].each() {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', status:MessageStatus.INBOUND)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver')
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		def poll = new Poll(title:'Miauow Mix')
		poll.addToResponses(chickenResponse)
		poll.addToResponses(liverResponse).save(failOnError:true, flush:true)
	}
	
	static createSearchTestMessages() {
		[new Fmessage(src:'Alex', dst:'+254987654', text:'meeting at 11.00', dateReceived: new Date()-1),
			new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob', dateReceived: new Date()-1),
				new Fmessage(src:'Michael', dst:'+2541234567', text:'Can we get meet in 5 minutes')].each() {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true)
				}

		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', status:MessageStatus.INBOUND)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver')
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
	}
	
	static createTestContacts() {
		[new Contact(name: 'Alice', primaryMobile: '+254778899'),
			new Contact(name: 'Bob', primaryMobile: '+254987654')].each() { it.save(failOnError:true) }
	}
	
}

