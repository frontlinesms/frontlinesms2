package frontlinesms2.controller

import frontlinesms2.*
class CustomFieldISpec extends grails.plugin.spock.IntegrationSpec {

	def "should return only the list of name that match all the custom field"(){ 
		when:
			def firstContact = new Contact(name:'Alex', primaryMobile:'+254987654').save(failOnError:true)
			def secondContact = new Contact(name:'Mark', primaryMobile:'+254333222').save(failOnError:true)
			def thirdContact = new Contact(name:"Toto", primaryMobile:'+666666666').save(failOnError:true)
	
			[new CustomField(name:'city', value:'Paris', contact: firstContact),
					new CustomField(name:'city', value:'Paris', contact: secondContact),
					new CustomField(name:'like', value:'cake', contact: secondContact),
					new CustomField(name:'like', value:'ca', contact: firstContact),
					new CustomField(name:'like', value:'ake', contact: thirdContact),
					new CustomField(name:'dob', value:'12/06/79', contact: secondContact),
					new Fmessage(src:'+666666666', dst:'+2549', text:'finaly i stay in bed', status:MessageStatus.INBOUND)].each {
				it.save(failOnError:true)
			}
		then:	
			CustomField.getAllContactNameMatchingCustomField([:])==['Alex','Mark','Toto']
			CustomField.getAllContactNameMatchingCustomField(['city':'Paris'])==['Alex','Mark']
			CustomField.getAllContactNameMatchingCustomField(['city':'paris'])==['Alex','Mark']
			CustomField.getAllContactNameMatchingCustomField(['city':'Paris','like':'ca'])==['Alex','Mark']
			CustomField.getAllContactNameMatchingCustomField(['city':'Paris','like':'ca','dob':'06'])==['Mark']
			CustomField.getAllContactNameMatchingCustomField(['city':'Paris','like':'ca','dob':'06','car':'yes'])==[]
	}
	
}
