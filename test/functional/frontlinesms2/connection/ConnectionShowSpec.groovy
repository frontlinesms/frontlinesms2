package frontlinesms2.connection

import frontlinesms2.*

class ConnectionShowSpec extends ConnectionGebSpec {
	def 'clicking on a connection shows us more details'() {
		given:
			createTestSMSConnection()
			to ConnectionListPage
		when:
			lstConnections.find('h2').click()
		then:
			$('title').text() == "Settings > Connections > MTN Dongle"
	}
	
	def "should update message count when in Settings section"() {
		when:
			to ConnectionListPage
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text: "message count", status: MessageStatus.INBOUND).save(flush: true, failOnError:true)
		then:
			$("#tab-messages").text() == "Messages 0"
		when:
			js.refreshMessageCount()
		then:
			waitFor{ 
				$("#tab-messages").text() == "Messages 1"
			}
	}
	
//FIXME: Build Fix
/*	def 'Send test message button for particular connection appears when that connection is selected and started'() {
		given:
			createTestConnection()
			def testyEmail = EmailFconnection.findByName('test email connection')
		when:
			go "connection/show/${testyEmail.id}"
		then:
			$('#connections .selected .test').isEmpty()
		when:
			$("#connections .selected .route").click()
		then:
			$('#connections .selected .test').@href == "/frontlinesms2/connection/createTest/${testyEmail.id}"
		cleanup:
			deleteTestConnections()
	}*/
}

class ConnectionShowPage extends geb.Page {
	static at = {
		assert title == "Settings > Connections > test email connection"
		true
	}
	
	static content = {
		lstConnections { $('#connections') }
		selectedConnection(required:false) { lstConnections.find(".selected") }
	}
}
