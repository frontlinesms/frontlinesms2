package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class RecipientLookupServiceISpec extends grails.plugin.spock.IntegrationSpec {
	@Shared
	def recipientLookupService
	private static final i18ns = [group:'Groups', smartGroup:'Smart Groups', contact:'Contacts', address:'Add phone number']

	def setup() {
		20.times {
			Contact.build(name:"test-contact-$it")	
			SmartGroup.build(name:"test-smartgroup-$it", mobile:"+543")	
			Group.build(name:"test-group-$it")	
		}
	}

	def 'lookup should not return a contact if he is already selected'() {
		when:
			def firstContactId = Contact.findByName('test-contact-1').id
			def results = recipientLookupService.lookup([term:"test-contact-1", 'selectedSoFar[]':"contact-${firstContactId}".toString()])
		then:
			getLookupResultFor(results, 'contact') == ['test-contact-10', 'test-contact-11', 'test-contact-12']
	}

	def 'lookup should echo back the search term'() {
		when:
			def results = recipientLookupService.lookup([term:"12"])
		then:
			results.query == '12'
	}

	def "lookup should return matching contacts, groups and smartgroups, as well as the raw contact name"() {
		when:
			def results = recipientLookupService.lookup([term:"12"])
		then:
			getLookupResultFor(results, "group") == ["test-group-12 (0)"]
			getLookupResultFor(results, "smartGroup") == ["test-smartgroup-12 (0)"]
			getLookupResultFor(results, "contact") == ["test-contact-12"]
			getLookupResultFor(results, "address") == ['"12"']
	}

	@Unroll
	def "lookup should only return addresses if they are valid phone numbers"() {
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
		when:
			def results = recipientLookupService.contactSearchResults([recipients:["contact-${Contact.getAll()[0].id}".toString(), "group-${Group.getAll()[0].id}".toString(), "smartgroup-${SmartGroup.getAll()[0].id}".toString(), "address-+12345"]])	
		then:
			results == [contacts:[Contact.getAll()[0]], groups:[Group.getAll()[0]], smartgroups:[SmartGroup.getAll()[0]], addresses:["+12345"]]	
	}

	private def getLookupResultFor(lookupResult, section) {
		return lookupResult.results.find { it.text == "contact.search.${section.toLowerCase()}" }?.items*.text
	}
}

