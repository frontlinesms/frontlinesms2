package frontlinesms2.controller

import frontlinesms2.*

class StatusControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new StatusController()
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
			}
	}
	
	def "should return a list of all available connections"() {
		when:
			def model = controller.show()
		then:
			model.connectionInstanceTotal == 2
			model.connectionInstanceList == [SmslibFconnection.findByName('MTN Dongle'), EmailFconnection.findByUsername('mr.testy@zoho.com')]
		when:
			SmslibFconnection.findByName('MTN Dongle').delete(flush:true)
			model = controller.show()
		then:
			model.connectionInstanceTotal == 1
			model.connectionInstanceList == [EmailFconnection.findByUsername('mr.testy@zoho.com')]
	}
	
	def "message start dates should be inclusive"() {
		given:
			def sentDate = createDate(2011, 10, 18, 23, 58, 59)
			def m1 = new Fmessage(src:"src1", date:createDate(2011, 10, 18, 0, 0, 1), inbound:true).save(flush:true, failOnError:true)
			def m2 = new Fmessage(src:"src2", date:sentDate, hasSent:true, inbound: false)
			def d = new Dispatch(dst:'123', status:DispatchStatus.SENT, dateSent:sentDate)
			m2.addToDispatches(d)
			m2.save(flush:true, failOnError:true)
		when:
			// TODO set start and end dates to the same day as messages were sent
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 0, 0, 0)
			controller.params.endDate = createDate(2011, 10, 18, 23, 59, 59)
			def model = controller.show()
		then:
			model.messageStats.sent == [1]
			model.messageStats.received == [1]
			
		when:
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 23, 58, 59)
			controller.params.endDate = createDate(2011, 10, 19, 0, 0, 0)
			model = controller.show()
		then:
			model.messageStats.sent == [1]
			model.messageStats.received == [1]
		when:
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 0, 0, 0)
			controller.params.endDate = createDate(2011, 10, 19, 23, 57, 59)
			model = controller.show()
		then:
			model.messageStats.sent == [1, 0]
			model.messageStats.received == [1, 0]
	}
	
	def "show action should return a list of filtered messages for the traffic graph"() {
		setup:
			createFilterTestData()
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = new Date() - 2
			controller.params.endDate = new Date()
		when:"general search"
			def model = controller.show()
		then:
			model.messageStats.sent == [2, 0, 1]
			model.messageStats.received == [0 , 0, 8]
			model.activityInstanceList.containsAll([Activity.findByName('test'), Poll.findByName('This is a poll')])
			model.folderInstanceList == [Folder.findByName('test')]
		when:"search within announcement messages"
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = new Date() - 2
			controller.params.endDate = new Date()
			def announcement = Activity.findByName('test')
			controller.params.activityId = "$announcement.id"
			model = controller.show()
		then:
			model.messageStats.sent == [0, 0, 1]
			model.messageStats.received == [0 , 0, 1]
			model.activityId == "$announcement.id"
			model.activityInstanceList.containsAll([Activity.findByName('test'), Poll.findByName('This is a poll')])
			model.folderInstanceList == [Folder.findByName('test')]
		when:"search within folder messages"
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = new Date() - 2
			controller.params.endDate = new Date()
			def folder = Folder.findByName('test')
			controller.params.activityId = "$folder.id"
			model = controller.show()
		then:
			model.messageStats.sent == [0, 0, 0]
			model.messageStats.received == [0 , 0, 1]
			model.activityId == "$folder.id"
			model.activityInstanceList.containsAll([Activity.findByName('test'), Poll.findByName('This is a poll')])
			model.folderInstanceList == [Folder.findByName('test')]
		when:"search within poll messages"
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = new Date() - 2
			controller.params.endDate = new Date()
			def poll = Poll.findByName('This is a poll')
			controller.params.activityId = "$poll.id"
			model = controller.show()
		then:
			model.messageStats.sent == [0, 0, 0]
			model.messageStats.received == [0 , 0, 2]
			model.activityId == "$poll.id"
			model.activityInstanceList.containsAll([Activity.findByName('test'), Poll.findByName('This is a poll')])
			model.folderInstanceList == [Folder.findByName('test')]
			
	}
	
	def createDate(int year, int month, int date, int hour=0, int minute=0, int second=0) {
		def calc = Calendar.getInstance()
		calc.set(year, month, date, hour, minute, second)
		calc.getTime()
	}
	
	def createFilterTestData() {
		[new Fmessage(src:'Bob', text:'I like manchester', date:new Date()),
			new Fmessage(src:'Bob', text:'I like manchester', date:new Date()),
			new Fmessage(src:'Bob', text:'I like manchester', date:new Date())].each {
				it.inbound = true
				it.save(flush:true, failOnError:true)
			}
		def dis1 = new Dispatch(dst: '12345', status: DispatchStatus.SENT, dateSent:new Date()-2)
		def sentMessage1 = new Fmessage(text:"sent message 1", inbound:false, date:new Date()-2, hasSent:true).addToDispatches(dis1).save(flush:true, failOnError:true)
		def dis2 = new Dispatch(dst: '34523', status: DispatchStatus.SENT, dateSent:new Date()-2)
		def sentMessage2 = new Fmessage(text:"sent message 2", inbound:false, date:new Date()-2, hasSent:true).addToDispatches(dis2).save(failOnError:true, flush:true)
		def message1 = new Fmessage(src:'Bob', inbound:true, text:'hi Bob', date:new Date(), starred: true).save(flush: true, failOnError:true)
		def message2 = new Fmessage(src:'Jim', inbound:true, text:'hi Bob', date:new Date()).save(flush: true, failOnError:true)
		def p = new Poll(name: 'This is a poll')
		p.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
		p.save(failOnError:true, flush:true)
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
}
