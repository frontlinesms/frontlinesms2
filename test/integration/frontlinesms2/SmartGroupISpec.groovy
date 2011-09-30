package frontlinesms2

class SmartGroupISpec extends grails.plugin.spock.IntegrationSpec {
	def 'phone number should match startsWith only'() {
		when:
			def x = 1 + 1
		then:
			x == 3
	}
	
	def 'contact name field should match anywhere in field'() {
		when:
			def x = 1 + 1
		then:
			x == 3
	}
	
	def 'custom field should match only specified custom field'() {
		when:
			def x = 1 + 1
		then:
			x == 3
	}
}