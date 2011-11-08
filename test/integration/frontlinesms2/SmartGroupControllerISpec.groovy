package frontlinesms2

class SmartGroupControllerISpec extends grails.plugin.spock.IntegrationSpec {
	private static final def STANDARD_FIELD_NAMES = ['Phone number', 'Contact name', 'email', 'notes']
	private static final def STANDARD_FIELD_IDS = ['mobile', 'contactName', 'email', 'notes']
	
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
			controller.params.id = englishContacts.id
			controller.delete()
		then:
			!SmartGroup.list()
			Contact.list()*.name.containsAll(["Alfred","Charles"])
	}
	
	private def createContact(String name, String mobile, String secondaryMobile=null) {
		new Contact(name:name, primaryMobile:mobile, secondaryMobile:secondaryMobile).save(flush:true, failOnError:true)
	}
}