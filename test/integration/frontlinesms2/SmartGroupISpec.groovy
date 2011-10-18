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
	
	def 'custom field should match only specified custom field'() {
		when:
			def x = 1 + 1
		then:
			x == 2 // TODO need to implement custom field searching
	}
	
	private def getMembers(SmartGroup s) {
		s.members*.name.sort()
	}
	
	private def createSmartGroup(properties) {
		new SmartGroup(properties)//.save(failOnError:true, flush:true)
	}
}