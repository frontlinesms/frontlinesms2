package frontlinesms2

class KeywordSpec extends grails.plugin.spock.UnitSpec {

	def "Keyword must have a value and an Activity"() {
		given:
			mockForConstraintsTests(Keyword)
			mockDomain(Activity)
		when:
			def k = new Keyword()
		then:
			!k.validate()
		when:
			k.activity = new Activity()
		then:
			!k.validate()
		when:
			k.value = "test"
		then:
			k.validate()
	}

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
