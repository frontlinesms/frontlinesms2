package frontlinesms2.controller

import frontlinesms2.*
class CustomFieldISpec extends grails.plugin.spock.IntegrationSpec {

	def "should return only the list of name that match all the custom field"(){ 
		when:
			def firstContact = new Contact(name:'Alex', mobile:'+254987654').save(failOnError:true)
			def secondContact = new Contact(name:'Mark', mobile:'+254333222').save(failOnError:true)
			def thirdContact = new Contact(name:"Toto", mobile:'+666666666').save(failOnError:true)
	
			[new CustomField(name:'city', value:'Paris', contact: firstContact),
					new CustomField(name:'city', value:'Paris', contact: secondContact),
					new CustomField(name:'like', value:'cake', contact: secondContact),
					new CustomField(name:'like', value:'ca', contact: firstContact),
					new CustomField(name:'like', value:'ake', contact: thirdContact),
					new CustomField(name:'dob', value:'12/06/79', contact: secondContact),
					new Fmessage(src:'+666666666', dst:'+2549', text:'finaly i stay in bed', inbound:true, date: new Date())].each {
				it.save(failOnError:true)
			}
		then:	
			CustomField.getAllContactsWithCustomField([:])==[firstContact,secondContact,thirdContact]
			CustomField.getAllContactsWithCustomField(['city':'Paris'])==[firstContact,secondContact]
			CustomField.getAllContactsWithCustomField(['city':'paris'])==[firstContact,secondContact]
			CustomField.getAllContactsWithCustomField(['city':'Paris','like':'ca'])==[firstContact,secondContact]
			CustomField.getAllContactsWithCustomField(['city':'Paris','like':'ca','dob':'06'])==[secondContact]
			CustomField.getAllContactsWithCustomField(['city':'Paris','like':'ca','dob':'06','car':'yes'])==[]
	}
	
}
