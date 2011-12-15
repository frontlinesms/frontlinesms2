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

	def "blank names are allowed so long as there is a number, there is no minimum length for name"() {
		setup:
			mockForConstraintsTests(Contact)
		when:
			def noNameContact = new Contact(name:'', primaryMobile:'9876543')
			def namedContact = new Contact(name:'a')
			def noInfoContact = new Contact(name:'', primaryMobile:'')
		then:
			noNameContact.validate()
			namedContact.validate()
			!noInfoContact.validate()
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
		when:
			Contact c = new Contact(name: 'Eve')
			c.addToCustomFields(new CustomField())
		then:
			c.validate()
	}

	def 'contact may have multiple custom fields'() {
		setup:
			mockDomain(Contact)
		when:
			Contact c = new Contact(name: 'Eve')
			c.addToCustomFields(new CustomField())
			c.addToCustomFields(new CustomField())
		then:
			c.validate()
	}

	def "should return the count of all messages sent to a given contact"() {
		setup:
			String johnsprimaryMobile = "9876543210"
			String johnssecondaryMobile = "123456789"
			Contact contact = new Contact(name: "John", primaryMobile: johnsprimaryMobile, secondaryMobile: johnssecondaryMobile)
			mockDomain Fmessage, [new Fmessage(dst: johnsprimaryMobile, deleted: false),
					new Fmessage(dst: johnsprimaryMobile, deleted: true),
					new Fmessage(dst: johnssecondaryMobile, deleted: true)]
	    when:
	        def count = contact.inboundMessagesCount
	    then:
	        count == 3
  	}

	def "should return the count of all messages received from a given contact"() {
		setup:
			String georgesAddress = "1234567890"
			String georgeAddress2 = "0987654151"
			String georgeEmail = "george@gmail.com"
			Contact contact = new Contact(name: "George", primaryMobile: georgesAddress, secondaryMobile: georgeAddress2, email: georgeEmail)
			mockDomain Fmessage, [new Fmessage(dst: georgesAddress, deleted: false),
					new Fmessage(src: georgesAddress, deleted: true),
					new Fmessage(src: georgesAddress, deleted: false),
					new Fmessage(dst: georgeAddress2, deleted: true),
					new Fmessage(src: georgeEmail, deleted: true)]
	    when:
	        def count = contact.outboundMessagesCount
	    then:                                     
	        count == 3
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
        setup:
			mockForConstraintsTests(Contact)
		when:
			def c = new Contact(notes: null, name: "Tim")
        then:
        	c.validate()
    }

   def 'should be able to add notes with length equal to 1024 chars'() {
	 	setup:
			mockForConstraintsTests(Contact)
        	def notes = "a" * 1024
        when:
			def c = new Contact(name: "Tim", notes: notes)
        then:
        	c.validate()
    }

   def 'should not be able to add notes with length more than 1024 chars'() {
		setup:
			mockForConstraintsTests(Contact)
			def notes = "a" * 1025
        when:
			def c = new Contact(name: "Tim", notes: notes)
        then:
			!c.validate()
   }
   
	def "the email address field can only contain a valid email address"() {
        setup:
			mockForConstraintsTests(Contact)
		when:
			def c = new Contact(name: "Tim", email: "yaya")
        then:
        	!c.validate()
		when:
			 c = new Contact(name: "Tim", email: "yaya@")
        then:
        	!c.validate()
		when:
			c = new Contact(name: "Tim", email: "yaya@gmail.com")
        then:
        	c.validate()
    }
	
	def "a contact can contain both primary and secondary mobile numbers"() {
		setup:
			mockForConstraintsTests(Contact)
        when:
			def c = new Contact(name: "Tim", primaryMobile: "+0724 356271", secondaryMobile: "+0723 467529")
        then:
        	c.validate()
	}
	
	def "a contact cannot contain the same primary and secondary mobile numbers"() {
		setup:
			mockForConstraintsTests(Contact)
        when:
			def c = new Contact(name: "Tim", primaryMobile: "+0724 356271", secondaryMobile: "+0724 356271")
        then:
        	!c.validate()
	}
	
	def "should delete optional contact fields"() {
		setup:
			mockForConstraintsTests(Contact)
        when:
			def c = new Contact(name: "Tim", primaryMobile: "+0724 356271", secondaryMobile: "+44 53 356271", email: "tim@tim.com")
        then:
        	c.validate()
        when:
        	c.secondaryMobile = null
        then:
        	c.validate()
        when:
        	c.email = null
        then:
        	c.validate()
	}
}

