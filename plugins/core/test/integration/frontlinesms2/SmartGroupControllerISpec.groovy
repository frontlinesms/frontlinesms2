package frontlinesms2

class SmartGroupControllerISpec extends grails.plugin.spock.IntegrationSpec {
	private static final def STANDARD_FIELD_NAMES = ['Phone number', 'Contact name', 'email', 'notes']
	private static final def STANDARD_FIELD_IDS = ['mobile', 'contactName', 'email', 'notes']
	private static final String CUSTOM_FIELD_ID_PREFIX = 'custom:'
	
	def controller
	
	def setup() {
		controller = new SmartGroupController()
	}
	
	def 'viewing smart group displays contents of that smart group'() {
		given:
			controller = new ContactController()
			def englishContacts = new SmartGroup(name:'English contacts', mobile:'+44').save(flush:true, failOnError:true)
			createContact('Alfred', '+4423456789')
			createContact('Bernadette', '+3323+4456789')
			createContact('Charles', '+440987654')
			createContact('Dupont', '+33098765432')
			createContact('Edgar de Gaulle', '+33098764677', '+44662848484')
		when:
			controller.params.smartGroupId = englishContacts.id
			def model = controller.show()
		then:
			model.contactInstanceList*.name == ['Alfred', 'Charles', 'Edgar de Gaulle']
			model.contactInstanceTotal == 3
		when:
			controller.params.searchString = 'ED'
			model = controller.show()
		then:
			model.contactInstanceList*.name == ['Alfred', 'Edgar de Gaulle']
			model.contactInstanceTotal == 2
	}
	
	def 'CREATE returns a smartgroup instance'() {
		when:
			def model = controller.create()
		then:
			model.smartGroupInstance instanceof SmartGroup
	}
	
	def 'CREATE returns a list of field names including standard field names'() {
		when:
			def model = controller.create()
		then:
			model.fieldNames == STANDARD_FIELD_NAMES
			model.fieldIds == STANDARD_FIELD_IDS
	}
	
	def 'CREATE returns a list of field names including custom field names'() {
		given:
			['Favourite Food', 'AIM name'].each { new CustomField(name:it).save(flush:true, failOnError:true) }
		when:
			def model = controller.create()
		then:
			model.fieldNames == STANDARD_FIELD_NAMES + ['AIM name', 'Favourite Food']
			model.fieldIds == STANDARD_FIELD_IDS + ['custom:AIM name', 'custom:Favourite Food']
	}
	
	def 'calling SAVE with a customfield rule defined will create a SmartGroup which owns a CustomField'() {
		given:
			controller.params.smartgroupname = 'Londons'
			controller.params.'rule-field' = ['custom:Town']
			controller.params.'rule-text' = ['London']
		when:
			controller.save()
			def g = SmartGroup.findByName('Londons')
		then:
			g
			g.customFields.size() == 1
		when:
			def cf = g.customFields.asList()[0]
		then:
			cf.name == 'Town'
			cf.value == 'London'
	}
	
	def 'can update smart group name'() {
		given:
			def smartGroup = new SmartGroup(name:'Smart Group', mobile:'+44').save(flush:true, failOnError:true)
			controller.params.id = smartGroup.id
			controller.params.smartgroupname = "renamed smart group"
		when:
			controller.save()
			def updatedGroup = SmartGroup.get(smartGroup.id)
		then:
			updatedGroup.name == "renamed smart group"
			controller.response.redirectedUrl == "/contact/show?smartGroupId=${smartGroup.id}"
	}
	
	def 'can update existing smartgroup mobile rules'() {
		given:
			def englishContacts = new SmartGroup(name:'English contacts', mobile:'+33').save(flush:true, failOnError:true)
			createContact('Alfred', '+4423456789')
			createContact('Bernadette', '+3323+4456789')
			createContact('Charles', '+440987654')
			createContact('Dupont', '+33098765432')
			createContact('Edgar de Gaulle', '+33098764677', '+44662848484')
			assert englishContacts.members*.name == ["Bernadette", "Dupont", "Edgar de Gaulle"]
		when:
			controller.params.smartgroupname = 'Londons'
			controller.params.id = "${englishContacts.id}"
			controller.params.'rule-text' = "+44"
			controller.params.'rule-field' = "mobile"
			controller.save()
		then:
			englishContacts.members*.name.sort() == ['Alfred', 'Charles', 'Edgar de Gaulle']
	}
	
	def 'can update only the smart group mobile rule when a smartgroup contains other rules'() {
		given:
			def englishContacts = new SmartGroup(name:'English contacts', mobile:'+33', notes:'test').save(flush:true, failOnError:true)
			def testContact1 = createContact('Alfred', '+4423456789')
			def testContact2 = createContact('Charles', '+442987654')
			testContact1.notes = "testing one two twa"
			testContact2.notes = "this is a test"
			testContact1.save(flush:true)
			testContact2.save(flush:true)
			def testContact3 = createContact('Bernadette', '+3323+4456789')
			testContact3.notes = "this is a test"
			testContact3.save(flush:true)
			createContact('Dupont', '+33098765432')
			createContact('Edgar de Gaulle', '+33098764677', '+44262848484')
			assert englishContacts.members*.name == ["Bernadette"]
			
		when:
			controller.params.smartgroupname = 'Londons'
			controller.params.id = "${englishContacts.id}"
			controller.params.'rule-text' = "+442"
			controller.params.'rule-field' = "mobile"
			controller.save()
		then:
			SmartGroup.get(englishContacts.id).name == "Londons"
			englishContacts.members*.name.sort() == ['Alfred', 'Charles']
	}
	
	def "can update the customfield rules of a smart group"() {
		given:
			def customFieldA = new CustomField(name:"location", value:"ken").save(flush:true)
			def customFieldB = new CustomField(name:"city", value:"nai").save(flush:true)
			
			def englishContacts = new SmartGroup(name:'English contacts', customFields:[customFieldA]).save(flush:true, failOnError:true)
			def testContact1 = createContact('Alfred', '+4423456789')
			def testContact2 = createContact('Charles', '+442987654')
			testContact1.addToCustomFields(new CustomField(name:"location", value:"Kenya"))
			testContact1.addToCustomFields(new CustomField(name:"city", value:"Dar es Salaam"))
			testContact2.addToCustomFields(new CustomField(name:"city", value:"Nairobi"))
			
			testContact1.save(flush:true)
			testContact2.save(flush:true)
			def testContact3 = createContact('Bernadette', '+3323+4456789')
			testContact3.addToCustomFields(new CustomField(name:"location", value:"Kenturky"))
			testContact3.save(flush:true)
		expect:
			englishContacts.members*.name == ["Alfred", "Bernadette"]
		when:
			controller.params.smartgroupname = 'English contacts'
			controller.params.id = "${englishContacts.id}"
			controller.params.'rule-text' = "nai"
			controller.params.'rule-field' = "${CUSTOM_FIELD_ID_PREFIX + customFieldB.name}"
			controller.save()
		then:
			englishContacts.members*.name.sort() == ['Charles']
	}
	
	def "can remove the customfield rules of a smart group"() {
		given:
			def customFieldA = new CustomField(name:"location", value:"ken").save(flush:true)
			def customFieldB = new CustomField(name:"city", value:"nai").save(flush:true)
			
			def englishContacts = new SmartGroup(name:'English contacts', customFields:[customFieldA, customFieldB]).save(flush:true, failOnError:true)
			def testContact1 = createContact('Alfred', '+4423456789')
			def testContact2 = createContact('Charles', '+442987654')
			def testContact3 = createContact('Bernadette', '+3323+4456789')
			
			testContact1.addToCustomFields(new CustomField(name:"location", value:"Kenya"))
			testContact1.addToCustomFields(new CustomField(name:"city", value:"Dar es Salaam"))
			testContact2.addToCustomFields(new CustomField(name:"city", value:"Nairobi"))
			testContact2.addToCustomFields(new CustomField(name:"location", value:"Kenya"))
			testContact3.addToCustomFields(new CustomField(name:"location", value:"Kenturky"))
			
			testContact1.save(flush:true)
			testContact2.save(flush:true)
			testContact3.save(flush:true)
		expect:
			englishContacts.members*.name == ["Charles"]
		when:
			controller.params.smartgroupname = 'English contacts'
			controller.params.id = "${englishContacts.id}"
			controller.params.'rule-text' = "ken"
			controller.params.'rule-field' = "${CUSTOM_FIELD_ID_PREFIX + customFieldA.name}"
			controller.save()
		then:
			englishContacts.members*.name.sort() == ['Alfred','Charles', 'Bernadette']
	}
	
	def 'calling DELETE should permanently remove a smart group and not its contacts'() {
		given:
			def englishContacts = new SmartGroup(name:'English contacts', mobile:'+44').save(flush:true, failOnError:true)
			createContact('Alfred', '+4423456789')
			createContact('Charles', '+440987654')
		when:
			def members = englishContacts.members
		then:
			members*.name == ['Alfred', 'Charles'] 
		when:
			controller.params.id = "${englishContacts.id}"
			controller.delete()
		then:
			!SmartGroup.list()
			Contact.list()*.name.containsAll(["Alfred","Charles"])
	}
	
	private def createContact(String name, String mobile, String secondaryMobile=null) {
		new Contact(name:name, primaryMobile:mobile, secondaryMobile:secondaryMobile).save(flush:true, failOnError:true)
	}
}
