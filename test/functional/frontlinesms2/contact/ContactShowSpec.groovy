package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactShowSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
	}

	def 'contacts link to their details'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go 'contact'
		then:
			def firstContactListItem = $('#contacts').children().first()
			def anchor = firstContactListItem.children('a').first()
			anchor.text() == 'Alice'
			anchor.getAttribute('href') == "/frontlinesms2/contact/show/${alice.id}"
	}

	def 'selected contacts show message statistics' () {
		given:
	  		def alice = Contact.findByName('Alice')
		when:
	  		go "contact/show/${alice.id}"
		then:
	        $("#message-count p").first().text() == '0 messages sent'
	        $("#message-count p").last().text() == '0 messages received'
	}

	def 'contact with no name can be clicked and edited because his primaryMobile is displayed'() {
		when:
			def empty = new Contact(name:'', primaryMobile:"+987654321")
			empty.save(failOnError:true)
			go "contact/list"
			def noName = Contact.findByName('')
		then:
			noName != null
			$('a', href:"/frontlinesms2/contact/show/${noName.id}").text().trim() == noName.primaryMobile
	}

	def 'selected contact is highlighted'() {
		given:
			def alice = Contact.findByName('Alice')
			def bob = Contact.findByName('Bob')
		when:
			go "contact/show/${alice.id}"
		then:
			assertContactSelected('Alice')
		    
		when:
			go "contact/show/${bob.id}"
		then:
			assertContactSelected('Bob')
	}

	def 'checked contact details are displayed'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go "contact/show/${alice.id}"
			$("#contact")[1].click()
			sleep 1000
		then:
			assertFieldDetailsCorrect('name', 'Name', 'Bob')
			$("#contact-title").text() == "Bob"
	}

	def 'contact with no groups has NO GROUPS message visible'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go "contact/show/${alice.id}"
		then:
			$('#no-groups').displayed
	}

	def 'contact with groups has NO GROUPS message hidden'() {
		given:
			createTestGroups()
			def bob = Contact.findByName('Bob')
		when:
			go "contact/show/${bob.id}"
		then:
			!$('#no-groups').displayed
		cleanup:
			deleteTestGroups()
	}
	
	def "clicking on 'Send Message' should redirect to quick message dialog"() {
		given:
	  		def alice = Contact.findByName('Alice')
		when:
	  		go "contact/show/${alice.id}"
			$("#contact-details .send-message").find{it.getAttribute('href').contains('2541234567')}.click()
			waitFor {$('div#tabs-1').displayed}
		then:
	        $('div#tabs-1').displayed
	}
	
	def "'send Message' link should not displayed for blank addresses"() {
		given:
	  		def alice = Contact.findByName('Alice')
		when:
	  		go "contact/show/${alice.id}"
		then:
			$("#contact-details .send-message").each {
	        	assert it.getAttribute('href') ==~ /.*recipients=\d+/
			}
	}
	
	def "sending a message to a contact address updates the statistics"() {
		given:
	  		def alice = Contact.findByName('Alice')
		when:
	  		go "contact/show/${alice.id}"
			$("#contact-details .send-message").find{it.getAttribute('href').contains('2541234567')}.click()
			waitFor {$('div#tabs-1').displayed}
			$("#nextPage").click()
			waitFor {$("#done").displayed}
        	$("#done").click()
			sleep 2000
		then:
			$("#confirm").click()
		when:
			go "contact/show/${alice.id}"
		then:
	        $("#message-count p").first().text() == '1 messages sent'
	        $("#message-count p").last().text() == '0 messages received'
		
	}

	def assertContactSelected(String name) {
		def selectedChildren = $('#contacts').children('li.selected')
		assert selectedChildren.size() == 1
		assert selectedChildren.text() == name
		true
	}
}

class EmptyContactPage extends geb.Page {
	static def getUrl() {
		"contact/show/${Contact.findByName('').id}"
	}

	static at = {
		assert url == "contact/show/${Contact.findByName('').id}"
		true
	}

	static content = {
		frmDetails { $("#contact-details") }
		btnSave { frmDetails.find('.update') }
	}
}
