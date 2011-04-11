import grails.plugin.geb.GebSpec

class SimpleSpec extends grails.plugin.geb.GebSpec {
	def 'should pass'() {
		given:
			def i = 1
		when:
			i += 1
		then:
			assert true
	}
	def 'should fail'() {
		given:
			def i = 1
		when:
			i += 1
		then:
			assert false
	}
}
