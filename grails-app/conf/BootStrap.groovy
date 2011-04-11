import grails.util.Environment
import frontlinesms2.Contact

class BootStrap {

	def init = { servletContext ->
		if (Environment.current == Environment.DEVELOPMENT) {
          	// do custom init for dev here
			createContact("Alice", "+123456789")
			createContact("Bob", "+198765432")
		}
    	}

	def createContact(String n, String a) {
		println "Creating contact: ${n}"
		def c = new Contact(name: n, address: a)
		c.save(failOnError: true)
	}

	def destroy = {
    }
}
