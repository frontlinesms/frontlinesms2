package frontlinesms2.domain

import frontlinesms2.*

class KeywordISpec extends grails.plugin.spock.IntegrationSpec {
	def "Keyword must have a value and an Activity"() {
		when:
			def k = new Keyword()
		then:
			!k.validate()
		when:
			k.activity = new Activity(name:'whatever')
		then:
			!k.validate()
		when:
			k.value = "test"
		then:
			k.validate()
	}
	
	def "keyword ust be unique unless its activity is archived"() {
		when:
			def k1 = new Keyword(value:'lock')
			def k2 = new Keyword(value:'lock')
			def activity1 = new Autoreply(name:'whatever1', autoreplyText: '1', archived: false, keyword: k1).save(flush:true, failOnError:true)
			def activity2 = new Autoreply(name:'whatever2', autoreplyText: '2', archived: false, keyword: k2).save(flush: true)
		then:
			println Keyword.getAll()
			!k1.validate()
			!k2.validate()
		when:
			activity1.archived = true
			activity1.save(failOnError: true, flush: true)
			activity2.save(failOnError: true, flush: true)
			k1.refresh()
			k2.refresh()
		then:
			k1.validate()
			k2.validate()
	}
}
