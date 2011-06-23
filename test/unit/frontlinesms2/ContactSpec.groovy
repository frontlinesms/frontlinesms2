package frontlinesms2

import grails.plugin.spock.*

class ContactSpec extends UnitSpec {
	def "contact may have a name"() {
		setup:
			mockForConstraintsTests(Contact)
		when:
			Contact c = new Contact()
			assert c.name == null
			c.name = 'Alice'
		then:
			c.name == 'Alice'
	}

	def "blank names are allowed, there is no minimum length for name"() {
		setup:
			mockForConstraintsTests(Contact)
		when:
			def noNameContact = new Contact(name:'', address:'9876543')
			def namedContact = new Contact(name:'a')
		then:
			noNameContact.validate()
			namedContact.validate()
	}

	def "duplicate names are allowed"(){
		setup:
			mockDomain(Contact)
		when:
			def Contact contact1 = new Contact(name:'John')
			def Contact contact2 = new Contact(name:'John')
		then:
			contact1.save()
			contact2.save()
	}

	def "max name length 255"(){
		setup:
			mockForConstraintsTests(Contact)
		when:
			def Contact contact = new Contact(name:'''\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef\
0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef''')
		then:
			!contact.validate()
	}

	def 'contact may have a custom field'() {
		setup:
			mockDomain(Contact)
			mockDomain(CustomField)
		when:
			Contact c = new Contact(name: 'Eve')
			c.addToCustomfields(new CustomField(name: 'height'))
		then:
			c.validate()
	}

	def 'contact may have multiple custom fields'() {
		setup:
			mockDomain(Contact)
		when:
			Contact c = new Contact(name: 'Eve')
			c.addToCustomfields(new CustomField())
			c.addToCustomfields(new CustomField())
		then:
			c.validate()
	}

	def "should return the count of all messages sent to a given contact"() {
		setup:
			String johnsAddress = "9876543210"
			Contact contact = new Contact(name: "John", address: johnsAddress)
			mockDomain Fmessage, [new Fmessage(dst: johnsAddress, deleted: false),
					new Fmessage(dst: johnsAddress, deleted: true),
					new Fmessage(dst: johnsAddress, deleted: true)]
	    when:
	        def count = contact.inboundMessagesCount
	    then:
	        count == 3
  	}

	def "should return the count of all messages received from a given contact"() {
		setup:
			String georgesAddress = "1234567890"
			Contact contact = new Contact(name: "George", address: georgesAddress)
			mockDomain Fmessage, [new Fmessage(dst: georgesAddress, deleted: false),
					new Fmessage(src: georgesAddress, deleted: true),
					new Fmessage(src: georgesAddress, deleted: false),
					new Fmessage(dst: georgesAddress, deleted: true)]
	    when:
	        def count = contact.outboundMessagesCount
	    then:                                     
	        count == 2
  	}

  	def "should return the count as zero is there is no address present for a given contact"() {
		when:
			def inboundMessagesCount = new Contact(name:"Person without an address").inboundMessagesCount
			def outboundMessagesCount = new Contact(name:"Person without an address").outboundMessagesCount
		then:
			inboundMessagesCount == 0
			outboundMessagesCount == 0
	}

    def "should not complain if a contact does not have a note"() {
        when:
			def c = new Contact(notes: null, name: "Tim")
        then:
        	c.validate()
    }

   def 'should be able to add notes with length equal to 1024 chars'() {
	 	setup:
        	def notes = "a" * 1024
        when:
			def c = new Contact(name: "Tim", notes: notes)
        then:
        	c.validate()
    }

   def 'should not be able to add notes with length more than 1024 chars'() {
		setup:
			def notes = "a" * 1025
        when:
			def c = new Contact(name: "Tim", notes: notes)
        then:
			!c.validate()
   }
}

