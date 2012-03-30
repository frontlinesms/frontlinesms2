package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*

class KeywordISpec extends grails.plugin.spock.IntegrationSpec {
	private static final def SIMPLE_ACTIVITY = new Activity(name:'whatever')
	
	@Unroll
	def "Keyword must have a value and an Activity"() {
		given:
			def k = new Keyword(value:word, activity:activity)
		expect:
			k.validate() == valid
		where:
			word   | activity        | valid
			null   | null            | false
			null   | SIMPLE_ACTIVITY | false
			'test' | null            | false
			'test' | SIMPLE_ACTIVITY | true
	}
	
	@Unroll
	def "keyword must be unique unless its activity is archived"() {
		given:
			def k1 = new Keyword(value:'lock')
		when:
			new Autoreply(name:'whatever1', autoreplyText:'1', archived:k1archived, keyword:k1).save(flush:true)
		then:
			k1.validate()
		when:
			def k2 = new Keyword(value:'lock')
			new Autoreply(name:'whatever2', autoreplyText:'2', archived:false, keyword:k2).save(flush:true)
		then:
			k2.validate() == k2valid
		where:
			k1archived | k2valid
			false      | false
			true       | true
	}
}

