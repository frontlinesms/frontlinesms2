package frontlinesms2

class SmartGroupControllerISpec extends grails.plugin.spock.ControllerSpec {
	def controller
	
	def setup() {
		controller = new ContactController()
	}
	
	def cleanup() {
		Contact.findAll()*.delete()
	}
	
	def 'viewing smart group displays contents of that smart group'() {
		given:
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
	
	private def createContact(String name, String mobile, String secondaryMobile=null) {
		new Contact(name:name, primaryMobile:mobile, secondaryMobile:secondaryMobile).save(flush:true, failOnError:true)
	}
}