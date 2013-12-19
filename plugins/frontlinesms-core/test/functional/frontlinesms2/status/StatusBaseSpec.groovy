package frontlinesms2.status

import frontlinesms2.*

class StatusBaseSpec extends grails.plugin.geb.GebSpec {
	private createTestMessages() {
		remote {
			(new Date()..new Date()-14).each {
				new TextMessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", inbound:true, text: "A message received on ${it}").save(failOnError:true, flush:true)
				new TextMessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", hasSent:true, text: "A message sent on ${it}")
					.addToDispatches(new Dispatch(dst: '12345', status: DispatchStatus.SENT, dateSent:new Date()-2))
					.addToDispatches(new Dispatch(dst: '23456', status: DispatchStatus.SENT, dateSent:new Date()-3))
					.save(failOnError:true, flush:true)
			}
			null
		}
	}

	private createTestTrafficGraphData() {
		remote {
			[new TextMessage(src:'Bob', dst:'+254987654', text:'I like manchester', date:new Date()),
				new TextMessage(src:'Bob', dst:'+254987654', text:'I like manchester', date:new Date()),
				new TextMessage(src:'Bob', dst:'+254987654', text:'I like manchester', date:new Date())].each {
					it.inbound = true
					it.save(flush:true, failOnError:true)
				}
			def dis1 = new Dispatch(dst: '12345', status: DispatchStatus.SENT, dateSent:new Date()-2)
			def sentMessage1 = new TextMessage(text:"sent message 1", inbound:false, date:new Date()-2, hasSent:true).addToDispatches(dis1).save(flush:true, failOnError:true)
			def dis2 = new Dispatch(dst: '34523', status: DispatchStatus.SENT, dateSent:new Date()-2)
			def sentMessage2 = new TextMessage(text:"sent message 2", inbound:false, date:new Date()-2, hasSent:true).addToDispatches(dis2).save(failOnError:true, flush:true)
			def message1 = new TextMessage(src:'Bob', inbound:true, text:'hi Bob', date:new Date(), starred: true).save(flush:true, failOnError:true)
			def message2 = new TextMessage(src:'Jim', inbound:true, text:'hi Bob', date:new Date()).save(flush:true, failOnError:true)
			def p = new Poll(name: 'This is a poll')
			p.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
			p.save(failOnError:true, flush:true)
			PollResponse.findByValue('Manchester').addToMessages(message1)
			PollResponse.findByValue('Barcelona').addToMessages(message2)
			p.save(flush:true, failOnError:true)

			def f = new Folder(name:'test').save(failOnError:true, flush:true)
			def m = new TextMessage(date: new Date(), inbound: true, src: 'src').save(failOnError:true, flush:true)
			f.addToMessages(m).save(flush:true)

			def announcementMessage = new TextMessage(text:"Test announcement", hasSent:true, inbound:false, date:new Date()).addToDispatches(new Dispatch(dst: '12345', status: DispatchStatus.SENT, dateSent:new Date())).save(flush:true, failOnError:true)
			def a = new Announcement(name:'test', messages: [new TextMessage(date: new Date(), src: 'src', inbound: true)]).save(failOnError:true, flush:true)
			a.addToMessages(announcementMessage)
			a.addToMessages(new TextMessage(date: new Date(), src: 'src', inbound: true))
			a.save(flush:true, failOnError:true)
			null
		}
	}

	def createTestConnections() {
		remote {
			[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
					new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
							serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
				it.save(flush:true, failOnError:true)
			}
			null
		}
	}

}
