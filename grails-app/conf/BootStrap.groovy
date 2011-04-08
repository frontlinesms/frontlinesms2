import frontlinesms2.Contact

class BootStrap {

    def init = { servletContext ->
		createContact("Alice", "+123456789")
		createContact("Bob", "+198765432")
    }

	def createContact(String n, String a) {
		def c = new Contact(name: n, address: a)
		c.save(failOnError: true)
	}

	def destroy = {
    }
}
