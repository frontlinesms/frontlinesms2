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
			def anchor = $(".displayName-${alice.id}")
			anchor.text() == 'Alice'
			anchor.@href.contains("/frontlinesms2/contact/show/$alice.id")
	}

	def 'selected contacts show message statistics' () {
		given:
	  		def alice = Contact.findByName('Alice')
		when:
	  		go "contact/show/$alice.id"
		then:
			$("#message-count p").first().text() == '0 messages sent'
			$("#message-count p").last().text() == '0 messages received'
	}

	def 'contact with no name can be clicked and edited because his primaryMobile is displayed'() {
		when:
			def empty = new Contact(name:'', primaryMobile:"+987654321")
			empty.save(failOnError:true, flush:true)
			go "contact/show/${empty.id}"
		then:
			$('a', href:"/frontlinesms2/contact/show/$empty.id?sort=&offset=").text().trim() == "+987654321"
	}

	def 'selected contact is highlighted'() {
		given:
			def alice = Contact.findByName('Alice')
			def bob = Contact.findByName('Bob')
		when:
			go "contact/show/$alice.id"
		then:
			assertContactSelected('Alice')
		when:
			go "contact/show/$bob.id"
		then:
			assertContactSelected('Bob')
	}

	def 'checked contact details are displayed'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go "contact/show/$alice.id"
			$(".contact-select")[1].click()
		then:	
			waitFor { $("#contact-title").text() == "Bob" }
			assertFieldDetailsCorrect('name', 'Name', 'Bob')
	}

	def 'contact with no groups has NO GROUPS message visible'() {
		given:
			def alice = Contact.findByName('Alice')
		when:
			go "contact/show/$alice.id"
		then:
			$('#no-groups').displayed
	}

	def 'contact with groups has NO GROUPS message hidden'() {
		given:
			createTestGroups()
			def bob = Contact.findByName('Bob')
		when:
			go "contact/show/$bob.id"
		then:
			!$('#no-groups').displayed
		cleanup:
			deleteTestGroups()
	}
	
	def "clicking on 'Send Message' should redirect to quick message dialog"() {
		given:
	  		def alice = Contact.findByName('Alice')
		when:
	  		go "contact/show/$alice.id"
			$("#contact_details .send-message").find { it.@href.contains('2541234567') }.click()
		then:	
			waitFor { $('div#tabs-1').displayed }
	}
	
	def "'send Message' link should not displayed for blank addresses"() {
		given:
	  		def alice = Contact.findByName('Alice')
		when:
	  		go "contact/show/$alice.id"
		then:
			$("#contact_details .send-message").each {
				assert it.@href ==~ /.*recipients=\d+/
			}
	}
	
	def "should update message count when in contacts tab"() {
		when:
			go "contact"
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text: "message count", status: MessageStatus.INBOUND).save(flush: true, failOnError:true)
		then:
			$("#tab-messages").text() == "Messages 0"
		when:
			js.refreshMessageCount()
		then:
			waitFor{ 
				$("#tab-messages").text() == "Messages 1"
			}
	}

	def assertContactSelected(String name) {
		def selectedChildren = $('#contact-list').children('li.selected')
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
