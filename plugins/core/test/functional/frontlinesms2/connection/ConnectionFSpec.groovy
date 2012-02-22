package frontlinesms2.connection

import frontlinesms2.*

class ConnectionFSpec extends ConnectionBaseSpec {
	def 'When there are no connections, this is explained to the user'() {
		when:
			to ConnectionPage
		then:
			lstConnections.tag() == 'div'
			lstConnections.text().contains('You have no connections configured.')
	}
	
	def 'There is a Not Connected label shown for inactive connection'() {
		when:
			createTestConnection()
			to ConnectionPage
		then:
			$('.connection-status')[0].text() == "Not connected"
	}
	
	def 'should show "create route" button for inactive connection '() {
		when:
			createTestConnection()
			to ConnectionPage
		then:
			$('.route')[0].text() == "Create route"
	}
//FIXME: Build Fix	
/*	def 'There is a Connected label shown for working connection'() {
		when:
			createTestConnection()
			to ConnectionPage
		then:
			$('div.status').text() == "Not connected"
		when:
			$(".buttons a").click()
		then:
			$('div.status').text() == "Connected"
		cleanup:
			deleteTestConnections()
	}*/
	
	def 'The first connection in the connection list page is selected'() {
		when: 
			createTestConnection()
			to ConnectionPage
		then:
			$('#connections .selected').size() == 1
	}
	
	def createTestConnections() {
		[new SmslibFconnection(name:'MTN Dongle', port:'COM99'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
		}
	}

	def deleteTestConnections() {
		SmslibFconnection.findAll().each() { it?.delete(flush: true) }
		EmailFconnection.findAll().each() { it?.delete(flush: true) }
		Fconnection.findAll().each() { it?.delete(flush: true) }
	}
	
	def 'clicking on a connection shows us more details'() {
		given:
			createTestSMSConnection()
			to ConnectionPage
		when:
			lstConnections.find('h2').click()
		then:
			$('title').text() == "Settings > Connections > MTN Dongle"
	}
	
	def "should update message count when in Settings section"() {
		when:
			to ConnectionPage
			def message = new Fmessage(src:'+254999999', text: "message count", inbound:true, date: new Date()).save(flush: true, failOnError:true)
		then:
			$("#message-tab-link").text().equalsIgnoreCase("Messages\n0")
		when:
			js.refreshMessageCount()
		then:
			waitFor { $("#message-tab-link").text().equalsIgnoreCase("Messages\n1") }
	}
	
//FIXME Tests fail due to the Quartz job scheduler
//	def 'Send test message button for particular connection appears when that connection is selected and started'() {
//			given:
//				createTestSMSConnection()
//				new SmslibFconnection(name:"test modem", port:"COM2", baud:"11200").save(flush:true, failOnError:true)
//				def testConnection = Fconnection.findByName('MTN Dongle')
//			when:
//				to ConnectionPage
//			then:
//				$('#connections .selected .test').isEmpty()
//			when:
//				waitFor{ $("#connections .selected .route").displayed }
//				$("#createRoute a").click()
//			then:
//				waitFor(15) { $('#notifications').text().contains("Created route")}
//				waitFor() {	$('#connections .selected').find("a.test").@href == "/connection/createTest/${testConnection.id}"}
//	}

//	def 'clicking "createRoute" should display a flash message when an exception occurs on a route'() {
//			given:
//				def testConnection = new SmslibFconnection(name:"test modem", port:"COM2", baud:"11200").save(flush:true, failOnError:true)
//			when:
//				to ConnectionPage
//				waitFor{ $("#connections .selected .route").displayed }
//				$("#createRoute a").click()
//			then:
//				waitFor { $('#notifications').text().contains("Failed")}
//	}
	
	def 'clicking Send test message takes us to a page with default message and empty recieving number field'() {
		given:
			createTestConnection()
		when:
			def testyEmail = EmailFconnection.findByName('test email connection')
			go "connection/createTest/${testyEmail.id}"
		then:
			assertFieldDetailsCorrect('addresses', 'Number', '')
			assertFieldDetailsCorrect('messageText', 'Message', "Congratulations from FrontlineSMS \\o/ you have successfully configured ${testyEmail.name} to send SMS \\o/")
	}

	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
		def label = $('label', for:fieldName)
		assert label.text() == labelText
		assert label.@for == fieldName
		def input
		if (fieldName == 'addresses') {
			input = $('input', name: fieldName)
		} else {
			input = $('textarea', name: fieldName)
		}
		assert input.@name == fieldName
		assert input.@id == fieldName
		assert input.@value  == expectedValue
		true
	}
	
}
