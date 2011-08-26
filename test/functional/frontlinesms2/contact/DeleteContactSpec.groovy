package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class DeleteContactSpec extends ContactGebSpec {
	def 'delete button is displayed and works'() {
		given:
			createTestContacts()
		when:
			go "contact/show/${Contact.findByName('Alice').id}"
			def deleteBtn = $('#btn_delete')
			withConfirm(true) { deleteBtn.click()}
		then:
			!Contact.findAllByName('Alice')
	}
	
	
	def 'should delete multiple selected contacts'() {
		given:
			createTestContacts()
			Contact.count() == 2
		when:
			go 'contact'
			$("#contact")[0].click()
			sleep 1000
			$("#contact")[1].click()
			sleep 1000
			
			def deleteBtn = $('#btn_delete_all')
			withConfirm(true) { deleteBtn.click()}
		then:
			Contact.count() == 0
			
		cleanup:
			deleteTestContacts()	
	}
}
