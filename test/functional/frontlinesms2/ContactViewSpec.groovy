package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactViewSpec extends grails.plugin.geb.GebSpec {
	def 'contacts list is displayed'() {
		when:
			go 'contact'
			println $('body').text()
		then:
			def contactsList = $('.list')
			println contactsList
			assert contactsList != null

			def odds = contactsList.find('.odd')
			assert odds[0].children()[2].text() == 'Alice'
		
			def contactNames = $('tbody').children().collect() {
				it.children()[2].text()
			}
			assert contactNames == ['Alice', 'Bob']
	}
}
