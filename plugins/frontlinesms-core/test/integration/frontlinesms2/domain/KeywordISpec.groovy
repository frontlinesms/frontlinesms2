package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*

class KeywordISpec extends grails.plugin.spock.IntegrationSpec {
	private static final def SIMPLE_ACTIVITY = Announcement.build(name:'whatever')
	
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
			'TEST' | null            | false
			'TEST' | SIMPLE_ACTIVITY | true
	}

	@Unroll
	def "keyword must be unique unless its activity is archived or deleted"() {
		given:
			def k1 = new Keyword(value:keyword)
		when:
			new Autoreply(name:'whatever1', autoreplyText:'1', archived:k1archived, deleted:k1deleted, keyword:k1).save(flush:true)
		then:
			k1.validate()
		when:
			def k2 = new Keyword(value:keyword)
			new Autoreply(name:'whatever2', autoreplyText:'2', keyword:k2).save(flush:true)
		then:
			k2.validate() == k2valid
		where:
			keyword | k1archived | k1deleted | k2valid
			''      | false      | false     | false
			''      | true       | false     | true
			''      | false      | true      | true
			''      | true       | true      | true
			'LOCK'  | false      | false     | false
			'LOCK'  | true       | false     | true
			'LOCK'  | false      | true      | true
			'LOCK'  | true       | true      | true
	}
}

