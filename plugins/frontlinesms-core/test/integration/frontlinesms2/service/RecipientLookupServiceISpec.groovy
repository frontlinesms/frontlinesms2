package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class RecipientLookupServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def recipientLookupService
	def i18ns = [group: "Groups", smartGroup: "Smartgroups", contact: "Contacts", address: "Add phone number"]

	def createTestData() {
		recipientLookupService.contactSearchService = new ContactSearchService()
		20.times {
			Contact.build(name:"test-contact-$it")	
			SmartGroup.build(name:"test-smartgroup-$it", mobile:"+543")	
			Group.build(name:"test-group-$it")	
		}
	}

	def "lookup should return matching contacts, groups and smartgroups, as well as the raw contact name"() {
		given:
			createTestData()
		when:
			def results = recipientLookupService.lookup("12")
		then:
			getLookupResultFor(results, "group") == ["test-group-12"]
			getLookupResultFor(results, "smartGroup") == ["test-smartgroup-12"]
			getLookupResultFor(results, "contact") == ["test-contact-12"]
			getLookupResultFor(results, "address") == ["\"12\""]
	}

	@Unroll
	def "lookup should only return addresses if they are valid phone numbers"() {
		given:
			createTestData()
		when:
			def results = recipientLookupService.lookup(query)
		then:
			getLookupResultFor(results, "address") == expectedResult
		where:
			query             | expectedResult
			"12"              | ["\"12\""]
			"+12"             | ["\"+12\""]
			"1 2"             | ["\"12\""]
			"1 2"             | ["\"12\""]
			"1 2"             | ["\"12\""]
			"1(2)a23"         | ["\"1223\""]
			"()test"          | null
	}

	private def getLookupResultFor(lookupResult, section) {
		return lookupResult.find { it.text == i18ns."$section" }?.items*.text
	}
}
