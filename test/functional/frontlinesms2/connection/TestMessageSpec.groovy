package frontlinesms2.connection

import frontlinesms2.*

class TestMessageSpec extends ConnectionGebSpec {
	def 'clicking Send test message takes us to a page with default message and empty recieving number field'() {
		given:
			createTestConnection()
		when:
			def testyEmail = EmailFconnection.findByName('test email connection')
			go "connection/createTest/${testyEmail.id}"
		then:
			assertFieldDetailsCorrect('number', 'Number', '')
			assertFieldDetailsCorrect('message', 'Message', "Congratulations from FrontlineSMS \\o/ you have successfully configured ${testyEmail.name} to send SMS \\o/")
		cleanup:
			deleteTestConnections()
	}

	def 'clicking submit with number field filled in sends message and displays flash message'() {
		given:
			createTestConnection()
		when:
			def testyEmail = EmailFconnection.findByName('test email connection')
			go "connection/createTest/${testyEmail.id}"
			$("#test-details").number = '345663211'
			def btnSend = $('#test-details #send')
			btnSend.click()
		then:
			at ConnectionTestPage
		cleanup:
			deleteTestConnections()

	}

	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
		def label = $('label', for:fieldName)
		assert label.text() == labelText
		assert label.getAttribute('for') == fieldName
		def input
		if (fieldName == 'number') {
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

class ConnectionTestPage extends geb.Page {
	static url = 'connection/sendTest/'
	static at = {
		assert title == "Settings > Connections > test email connection"
		true
	}
}

