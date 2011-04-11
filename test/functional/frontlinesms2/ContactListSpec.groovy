package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactListSpec extends grails.plugin.geb.GebSpec {
	def 'contacts list is displayed'() {
		when:
			go 'contact'
			println $('body').text()
		then:
			def contactDetails = $('#contacts')
			
			def firstContactListItem = $('#contacts').children().first()
			println " firstContactListItem: ${firstContactListItem}"
			println " firstContactListItem.children(): ${firstContactListItem.children().collect() { it.tag() }}"
			def anchor = firstContactListItem.children('a').first()
			assert anchor.text() == 'Alice'
			assert anchor.getAttribute('href') == '/frontlinesms2/contact/show/1'
	}
}
