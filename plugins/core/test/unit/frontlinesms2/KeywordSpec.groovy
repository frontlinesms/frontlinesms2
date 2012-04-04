package frontlinesms2

import spock.lang.*

class KeywordSpec extends grails.plugin.spock.UnitSpec {

	def "beforeSave should convert keyword to upper case"() {
		given:
			Keyword k = new Keyword(value:'shoehorn')
		when:
			k.beforeSave()
		then:
			k.value == 'SHOEHORN'
	}
	
	@Unroll
	def 'Keyword value constraints test'() {
		given:
			mockForConstraintsTests Keyword
			registerMetaClass Keyword
			Keyword.metaClass.static.findAllByValue = { v -> [] }
		when:
			def k = new Keyword(value:keyword, activity:Mock(Activity))
		then:
			k.validate() == valid
		where:
			valid | keyword
			false | null
			true  | ''
			false | 'lowercase'
			true  | 'UPPERCASE'
			false | 'WITH SPACE'
	}
}

