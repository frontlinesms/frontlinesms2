package frontlinesms2.help

import frontlinesms2.*

class HelpSpec extends grails.plugin.geb.GebSpec {
	
	def 'clicking an item in the help index brings up its text in the right pane'() {
		when:	
			go 'message/inbox'
			$("#system-nav li:nth-child(2) a").click()
		then:
			waitFor { $("#help").displayed }
		when:
			$("#help #index li", text: "How do I get around the Messages screen?").click()
		then:
			$("#help #file").text().contains('test')
	}
}
