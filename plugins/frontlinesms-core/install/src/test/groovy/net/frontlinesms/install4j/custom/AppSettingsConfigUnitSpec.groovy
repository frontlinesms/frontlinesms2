package net.frontlinesms.install4j.custom

import spock.lang.*

class AppSettingsConfigUnitSpec extends Specification{
	def "updating status of version checking should persist correct values"(){
		given:
			AppSettingsConfig.setAppProperty('version.check.updates',inValue)
		expect:
			AppSettingsConfig.getAppProperty('version.check.updates') == outValue
		where:
			inValue | outValue
			'true'  | 'true'
			'false' | 'false'
	}
}