package frontlinesms2

class ContactSpecUtils {
	static createTestContacts() {	
		[new Contact(name: 'Alice', address: '+2541234567'),
			new Contact(name: 'Bob', address: '+254987654')].each() { it.save(failOnError:true) }
	}

	static deleteTestContacts() {
		Contact.findAll().each() {
			it.delete(failOnError:true, flush:true)		
		}
	}
}
