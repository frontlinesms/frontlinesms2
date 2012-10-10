package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(Keyword)
class KeywordSpec extends Specification {
	def "beforeSave should convert keyword to upper case"() {
		given:
			Keyword k = new Keyword(value:'shoehorn', isTopLevel: true)
		when:
			k.beforeSave()
		then:
			k.value == 'SHOEHORN'
	}
	
	@Unroll
	def 'Keyword value constraints test'() {
		given:
			Keyword.metaClass.static.findAllByValue = { v -> [] }
		when:
			def k = new Keyword(value:keyword, ownerDetail:ownerDetail, isTopLevel:isTopLevel, activity:Mock(Activity))
		then:
			k.validate() == valid
		where:
			valid | keyword      | ownerDetail | isTopLevel
			false | null         | null        | true
			true  | ''           | null        | true
			false | 'lowercase'  | null        | true
			true  | 'UPPERCASE'  | null        | true
			false | 'WITH SPACE' | null        | true
			true  | 'UPPERCASE'  | 'something' | false
			false | 'UPPERCASE'  | null        | false
	}
}

