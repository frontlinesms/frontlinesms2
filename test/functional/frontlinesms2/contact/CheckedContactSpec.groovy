package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class CheckedContactSpec extends ContactGebSpec {

	def cleanup() {
		deleteTestContacts()
	}
	
	def 'should update screen to show number of selected messages'() {
		given:
			createTestContacts()
		when:
			go 'contact'
			$("#contact")[1].click()
			sleep 1000
			$("#contact")[0].click()
			sleep 1000
		then:
			$("#contact")[0].@checked == "true"
			$("#contact")[1].@checked == "true"
			$('#count').text() == '2 contacts selected'
	}

}
