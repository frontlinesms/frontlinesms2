package frontlinesms2.contact

import frontlinesms2.*
import frontlinesms2.search.PageSearchResult

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class ContactViewSpec extends ContactBaseSpec {
	def setup() {
		createTestContacts()
	}
	
	def 'should update screen to show number of selected messages'() {
		when:
			to PageContactShow
			contactSelect[1].click()
		then:
			waitFor { $('input', name:'name').value() == 'Bob' }
		when:
			contactSelect[0].click()
		then:
			waitFor { contactCount.text() == '2 contacts selected' }
			contactSelect[0].checked
			contactSelect[1].checked
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
		when:
			to PageContactShowAlice
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
		when:
			to PageContactShowAlice
		then:
			assertContactSelected('Alice')
		when:
			to PageContactShowBob
		then:
			assertContactSelected('Bob')
	}

	def 'checked contact details are displayed'() {
		when:
			to PageContactShowAlice
			$(".contact-select")[1].click()
		then:	
			waitFor { $("#contact-title").text() == "Bob" }
			assertFieldDetailsCorrect('name', 'Name', 'Bob')
	}

	def 'contact with no groups has NO GROUPS message visible'() {
		when:
			to PageContactShowAlice
		then:
			$('#no-groups').displayed
	}

	def 'contact with groups has NO GROUPS message hidden'() {
		given:
			createTestGroups()
		when:
			to PageContactShowBob
		then:
			!$('#no-groups').displayed
		cleanup:
			deleteTestGroups()
	}
	
	def "clicking on 'Send Message' should redirect to quick message dialog"() {
		when:
			to PageContactShowAlice
			$("#contact_details .send-message").find { it.@href.contains('2541234567') }.click()
		then:	
			waitFor { $('div#tabs-1').displayed }
	}
	
	def "'send Message' link should not displayed for blank addresses"() {
		when:
			to PageContactShowAlice
		then:
			$("#contact_details .send-message").each {
				assert it.@href ==~ /.*recipients=\d+/
			}
	}
	
	def "should update message count when in contacts tab"() {
		when:
			to PageContactShow
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

	def "clicking on search should only shows contact's messages"(){
		setup:
			createTestMessages()
		when:
			to PageContactShowAlice
			searchBtn.click()
		then:
			at PageSearchResult
			messageList.each { assert it.find("td:nth-child(3)").text() == 'Alice' }
	}

	def assertContactSelected(String name) {
		def selectedChildren = $('#contact-list').children('li.selected')
		assert selectedChildren.size() == 1
		assert selectedChildren.text() == name
		true
	}
}