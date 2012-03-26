package frontlinesms2

class SmartGroupISpec extends grails.plugin.spock.IntegrationSpec {
	def setup() {
		// English contacts
		def english = [
			['Alice Apple', '+447890123456', 'she sure likes apples', 'alice@example.co.uk'],
			['Bob Burnquist', '+447890987654', 'not a big fan of apples, except for granny smiths', 'bob@example.com'],
			['Charlie Charlesworth', '+44666666666', 'would sell his own granny'],
			['Darren Devonshire', '+447890254+254']
		]
		
		// Kenyan contacts
		def kenyan = [
			['Grace Githeri', '+25470123456', 'safaricom.  a lovely old granny, she is', 'grace@example.co.ke'],
			['Horace Ugali', '+25470987654', 'safaricom'],
			['Ndungu Ndengu', '+2547123456', 'airtel', 'ndungu@example.com'],
			['Tricky Tusker', '+254714444+44', 'airtel', 'tricky.example.com@mystupiddomain.com']
		]
		
		(kenyan + english).each {
			def notes = it[2] ?: ''
			def email = it[3] ?: ''
			new Contact(name:it[0], primaryMobile:it[1], notes:notes, email:email).save(failOnError:true, flush:true)
		}
	}
	
	def 'phone number should match startsWith only'() {
		when:
			def s = createSmartGroup(name:'English numbers', mobile:'+44')
		then:
			getMembers(s) == ['Alice Apple', 'Bob Burnquist', 'Charlie Charlesworth', 'Darren Devonshire']
	}
	
	def 'contact name should match anywhere in field'() {
		when:
			def s = createSmartGroup(name:'All the Us', contactName:'u')
		then:
			getMembers(s) == ['Bob Burnquist', 'Horace Ugali', 'Ndungu Ndengu', 'Tricky Tusker']
	}

	def 'email should match anywhere in field'() {
		when:
			def s = createSmartGroup(name:'example.com', email:'example.com')
		then:
			getMembers(s) == ['Bob Burnquist', 'Ndungu Ndengu', 'Tricky Tusker']
	}
	
	def 'notes field should match anywhere in field'() {
		when:
			def s = createSmartGroup(name:'Grannies!', notes:'granny')
		then:
			getMembers(s) == ['Bob Burnquist', 'Charlie Charlesworth', 'Grace Githeri']
			
		when:
			s = createSmartGroup(name:'Grannies!', notes:'apple')
		then:
			getMembers(s) == ['Alice Apple', 'Bob Burnquist']
	}
	
	def 'customfield should match only specified customfield'() {
		given:
			createContact(name:'Wov', shoe:'flip flop')
			createContact(name:'Xav', shoe:'brogue')
			createContact(name:'Yaz', shoe:'clog')
			createContact(name:'Zab', hat:'clog')
		when:
			def s = createSmartGroup(name:'Cloggers').addToCustomFields(new CustomField(name:'shoe', value:'lo'))
		then:
			getMembers(s) == ['Wov', 'Yaz']
	}

	def 'customfield should match only specified customfields'() {
		given:
			createContact(name:'Wov', shoe:'flip flop', hat:'stetson')
			createContact(name:'Xav', shoe:'brogue', hat:'trilby')
			createContact(name:'Yaz', shoe:'clog', hat:'bowler')
			createContact(name:'Zab', shoe:'stetson', hat:'clog')
		when:
			def s = createSmartGroup(name:'Cloggers')
			s.addToCustomFields(new CustomField(name:'shoe', value:'lo'))
			s.addToCustomFields(new CustomField(name:'hat', value:'so'))
		then:
			getMembers(s) == ['Wov']
	}
	
	def "can edit a smartgroup's properties'"() {
		given:
			createContact(name:'Wov', shoe:'flip flop', hat:'stetson')
		when:
			def s = createSmartGroup(name:'English numbers', mobile:'+44')
		then:
			getMembers(s) == ['Alice Apple', 'Bob Burnquist', 'Charlie Charlesworth', 'Darren Devonshire']
		when:
			s.name = "Cloggers"
			s.mobile = "+254"
			s.save(flush:true)
			s.refresh()
		then:
			s.name == "Cloggers"
			s.mobile == "+254"
			getMembers(s) == ['Grace Githeri', 'Horace Ugali', 'Ndungu Ndengu', 'Tricky Tusker']
	}
	
	def "smartGroup members should be updated when the custom field value is updated"() {
		given:
			def customFieldA = new CustomField(name:"location", value:"ken").save(flush:true)
			
			def smartGroup = new SmartGroup(name:'English contacts', customFields:[customFieldA]).save(flush:true, failOnError:true)
			def testContact1 = createContact(name:'Alfred', primaryMobile:'+4423456789')
			def testContact2 = createContact(name:'Charles', primaryMobile:'+442987654')
			def testContact3 = createContact(name:'Bernadette', primaryMobile:'+3323+4456789')
			
			testContact1.addToCustomFields(new CustomField(name:"location", value:"Kenya"))
			testContact1.addToCustomFields(new CustomField(name:"city", value:"Dar es Salaam"))
			testContact2.addToCustomFields(new CustomField(name:"city", value:"Nairobi"))
			testContact3.addToCustomFields(new CustomField(name:"location", value:"Kenturky"))
			
			testContact1.save(flush:true)
			testContact2.save(flush:true)
			testContact3.save(flush:true)
		expect:
			smartGroup.members*.name == ["Alfred", "Bernadette"]
		when:
			smartGroup.removeFromCustomFields(customFieldA)	
			smartGroup.addToCustomFields(new CustomField(name:"location", value:"kenya"))
			smartGroup.save(flush:true)
		then:
			smartGroup.members*.name.sort() == ['Alfred']
	}
	
	private def getMembers(SmartGroup s) {
		s.members*.name.sort()
	}
	
	private def createSmartGroup(properties) {
		new SmartGroup(properties)//.save(failOnError:true, flush:true)
	}
	
	private def createContact(params) {
		def c = new Contact(name:params.name)
		if(params.shoe) c.addToCustomFields(new CustomField(name:'shoe', value:params.shoe))
		if(params.hat) c.addToCustomFields(new CustomField(name:'hat', value:params.hat))
		c.save(failOnError:true, flush:true)
	}
}
