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

