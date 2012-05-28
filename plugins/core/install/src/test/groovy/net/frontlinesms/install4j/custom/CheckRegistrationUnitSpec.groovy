package net.frontlinesms.install4j.custom

import spock.lang.*

class CheckRegistrationUnitSpec extends Specification {
	def "isRegistered should return valid registration status if software is registered"(){
		expect:
			setContentsOfPropFile(uuid,status).equals(valid)
		where:
			uuid 	| status| valid
			"3456"	| true 	| true
			"9876"	| false | false
	}

	def setContentsOfPropFile(String uuid, boolean status){
		Futil.createRegistrationPropertiesFile(uuid, status)
		new CheckRegistration().isRegistered()
	}
}