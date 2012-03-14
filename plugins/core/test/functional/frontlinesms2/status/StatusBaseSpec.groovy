package frontlinesms2.status

import frontlinesms2.*


class StatusBaseSpec extends grails.plugin.geb.GebSpec {
	
	private createTestMessages() {
		(new Date()..new Date()-14).each {
			new Fmessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", inbound:true, text: "A message received on ${it}").save()
			new Fmessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", hasSent:true, text: "A message sent on ${it}").save()
		}
	}
	
	private createTestTrafficGraphData() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', date:new Date()),
			new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', date:new Date()),
			new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', date:new Date())].each {
				it.inbound = true
				it.save(flush:true, failOnError:true)
			}
		def dis1 = new Dispatch(dst: '12345', status: DispatchStatus.SENT, dateSent:new Date()-2)
		def sentMessage1 = new Fmessage(text:"sent message 1", inbound:false, date:new Date()-2, hasSent:true).addToDispatches(dis1).save(flush:true, failOnError:true)
		def dis2 = new Dispatch(dst: '34523', status: DispatchStatus.SENT, dateSent:new Date()-2)
		def sentMessage2 = new Fmessage(text:"sent message 2", inbound:false, date:new Date()-2, hasSent:true).addToDispatches(dis2).save(failOnError:true, flush:true)
		def message1 = new Fmessage(src:'Bob', inbound:true, text:'hi Bob', date:new Date(), starred: true).save(flush: true, failOnError:true)
		def message2 = new Fmessage(src:'Jim', inbound:true, text:'hi Bob', date:new Date()).save(flush: true, failOnError:true)
		def p = Poll.createPoll(name: 'This is a poll', choiceA: 'Manchester', choiceB:'Barcelona').save(failOnError:true, flush:true)
		PollResponse.findByValue('Manchester').addToMessages(message1)
		PollResponse.findByValue('Barcelona').addToMessages(message2)
		p.save(flush:true, failOnError:true)
		
		def f = new Folder(name:'test').save(failOnError:true)
		def m = new Fmessage(date: new Date(), inbound: true, src: 'src').save(failOnError:true)
		f.addToMessages(m).save(flush:true)
		
		def announcementMessage = new Fmessage(text:"Test announcement", hasSent:true, inbound:false, date:new Date()).addToDispatches(new Dispatch(dst: '12345', status: DispatchStatus.SENT, dateSent:new Date())).save(flush:true, failOnError:true)
		def a = new Announcement(name:'test', messages: [new Fmessage(date: new Date(), src: 'src', inbound: true)]).save(failOnError:true)
		a.addToMessages(announcementMessage)
		a.addToMessages(new Fmessage(date: new Date(), src: 'src', inbound: true))
		a.save(flush:true, failOnError:true)
	}
	
	def createTestConnections() {
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
		}
	}

}
