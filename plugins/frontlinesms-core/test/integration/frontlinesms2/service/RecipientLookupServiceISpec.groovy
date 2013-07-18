package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class RecipientLookupServiceISpec extends grails.plugin.spock.IntegrationSpec {
	/*
	* TODO: UNCOMMENT THIS ONCE CORE-1703 IS MERGED TO MASTER
	*

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
			def results = recipientLookupService.lookup([term:"12"])
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
			query                | expectedResult
			[term:"12"]          | ["\"12\""]
			[term:"+12"]         | ["\"+12\""]
			[term:"1 2"]         | ["\"12\""]
			[term:"1 2"]         | ["\"12\""]
			[term:"1 2"]         | ["\"12\""]
			[term:"1(2)a23"]     | ["\"1223\""]
			[term:"()test"]      | null
	}

	def "contactSearchResults() should return the selected groups, contacts, smartgroups and addresses"() {
		given:
			createTestData()
		when:
			def results = recipientLookupService.contactSearchResults([recipients:["contact-${Contact.getAll()[0].id}".toString(), "group-${Group.getAll()[0].id}".toString(), "smartgroup-${SmartGroup.getAll()[0].id}".toString(), "address-+12345"]])	
		then:
			results == [contacts:[Contact.getAll()[0]], groups:[Group.getAll()[0]], smartgroups:[SmartGroup.getAll()[0]], addresses:["+12345"]]	
	}

	private def getLookupResultFor(lookupResult, section) {
		return lookupResult.find { it.text == i18ns."$section" }?.items*.text
	}
	*/
}
