package frontlinesms2.contact

import frontlinesms2.*
import frontlinesms2.search.*

import geb.Browser
import grails.plugin.geb.GebSpec

class ContactListSpec extends ContactBaseSpec {
	def 'contacts list is displayed'() {
		given:
			createTestContacts()
		when:
			to PageContactShow
		then:	
			contactList.contacts.containsAll(['Alice', 'Bob'])
	}

	def 'contacts list not shown when no contacts exist'() {
		when:
			to PageContactShow
		then:
			contactList.noContent == 'contact.list.no.contact'
	}

	def 'ALL CONTACTS menu item is selected in default view'() {
		when:
			to PageContactShow
		then:
			bodyMenu.selectedMenuItem ==~ /contact.all.contacts\[\d+\]/
	}
	
	def 'contacts list is paginated'() {
		given:
			createManyContacts()
		when:
			to PageContactShow
		then:
			def contactNames = contactList.contacts - "Select All"
			def expectedNames = (11..60).collect { "Contact${it}" }
			assert contactNames == expectedNames
	}
	
	def 'should be able to search contacts'() {
		given:
			remote {
				Contact.build(name:'Sam Anderson')
				Contact.build(name:'SAm Jones')
				Contact.build(name:'SaM Tina')
				Contact.build(name:'bob')
				null
			}
		when:
			to PageContactShow
			footer.searchContact.jquery.trigger('focus')
			footer.searchContact << "Sam"
		then:
			waitFor { contactList.contacts.containsAll(['Sam Anderson', 'SAm Jones', 'SaM Tina']) && contactList.contacts.size() == 3 }
	}

	def 'should be able to search contacts by phone number'() {
		given:
			remote {
				Contact.build(name:'Sam Anderson', mobile:"+11111")
				Contact.build(name:'SAm Jones', mobile:"+11112")
				Contact.build(name:'SaM Tina', mobile:"+23232")
				Contact.build(name:'bob')
				null
			}
		when:
			to PageContactShow
			footer.searchContact.jquery.trigger('focus')
			footer.searchContact << "+1111"
		then:
			waitFor { contactList.contacts.containsAll(['Sam Anderson', 'SAm Jones']) }
	}
	
	def 'should be able to search contacts within a group'() {
		given:
			def friendsGroupId = remote {
				def fpGroup = Group.build(name:"Friends")
				def samAnderson = Contact.build(name:'Sam Anderson')
				def samJones = Contact.build(name:'SAm Jones')
				def samTina = Contact.build(name:'SaM Tina')
				def bob = Contact.build(name:'Bob')

				samAnderson.addToGroups(fpGroup, true)
				samJones.addToGroups(fpGroup, true)
				bob.addToGroups(fpGroup, true)

				return fpGroup.id
			}
		when:
			to PageContactShow, friendsGroupId
			footer.searchContact.jquery.trigger('focus')
			footer.searchContact << "Sam"
		then:
			waitFor { contactList.contacts.containsAll(['SAm Jones', 'Sam Anderson']) }
	}
	
	def "should remain on the same page when a contact is selected"() {
		given:
			createManyContacts()
		when:
			to PageContactShow
			footer.nextPage.click()
		then:
			!footer.prevPage.disabled
		when:
			contactList.selectContact 1
		then:
			!footer.prevPage.disabled
	}

	def "can select all contacts using checkbox"() {
		given:
			createManyContacts()
		when:
			to PageContactShow
			contactList.selectAll.click()
		then:
			waitFor('veryslow') {multipleContactDetails.checkedContactCount == 50}
	}

	def "should deselect 'select all' when some contacts are not selected"() {
		given:
			createManyContacts()
		when:
			to PageContactShow
			contactList.selectAll.click()
		then:
			waitFor('veryslow') {multipleContactDetails.checkedContactCount == 50}
			contactList.selectAll.checked
		when:
			contactList.selectContact 1
		then:
			!contactList.selectAll.checked
	}

	def '"All contacts" heading should displayed contact count'() {
		given:
			createTestContacts()
		when:
			to PageContactShow
		then:	
			header.contactCount == 2
	}

	def 'can search for all messages from a named contact'() {
		given:
			remote {
				def pedro = Contact.build(name:'Pedro', mobile:'+111')
				(1..20).each { Fmessage.build(src:'+111', inbound:true, text:"message ${it}") }
				def outgoingMsg = new Fmessage(src:'000', inbound:false, text:"outgoing message to Pedro")
						.addToDispatches(dst:"+111", status:DispatchStatus.SENT, dateSent:new Date())
						.save(failOnError:true, flush:true)
				null
			}
		when:
			to PageContactShow
			contactList.selectContact 0
		then:
			waitFor { singleContactDetails.name.value() == "Pedro" }
		when:
			singleContactDetails.searchForMessages.click()
		then:
			waitFor { at PageSearchResult }
			messageList.messageCount() == 21
	}

	def 'can search for all messages from an unnamed contact'() {
		given:
			remote {
				def pedro = Contact.build(name:'', mobile:'+111')
				(1..20).each { Fmessage.build(src:'+111', inbound:true, text:"message ${it}") }
				def outgoingMsg = new Fmessage(src:'000', inbound:false, text:"outgoing message to Pedro")
						.addToDispatches(dst:"+111", status:DispatchStatus.SENT, dateSent:new Date())
						.save(failOnError:true, flush:true)
				null
			}
		when:
			to PageContactShow
			contactList.selectContact 0
		then:
			waitFor { singleContactDetails.name.value() == "" }
		when:
			singleContactDetails.searchForMessages.click()
		then:
			waitFor { at PageSearchResult }
			messageList.messageCount() == 21
	}
}

