package frontlinesms2

class KeywordSpec extends grails.plugin.spock.UnitSpec {

	def "beforeSave should convert keyword to upper case"() {
		given:
			Keyword k = new Keyword(value:'shoehorn')
		when:
			k.beforeSave()
		then:
			k.value == 'SHOEHORN'
	}
	
	def "Keyword may not contain whitespace"() {
		given:
			mockForConstraintsTests(Keyword)
		when:
			def k = new Keyword(value:'with space')
		then:
			!k.validate()
			k.errors.value
	}
}
