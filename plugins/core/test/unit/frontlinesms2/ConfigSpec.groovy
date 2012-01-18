package frontlinesms2

import grails.plugin.spock.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder as GrailsConfig

class ConfigSpec extends UnitSpec {
	
	def 'frontlinesms plugin property should be set to core when no plugins are installed'() {
		setup:
			mockConfig('''
				frontlinesms2 {
					plugin = 'core'
				}
			''')
		when:
			def plugin = GrailsConfig.config.frontlinesms2.plugin
		then:
			plugin == "core"
	}
	
	def 'frontlinesms plugin property should be set to radio when radio plugin is installed'() {
		setup:
			mockConfig('''
				frontlinesms2 {
					plugin = 'radio'
				}
			''')
		when:
			def plugin = GrailsConfig.config.frontlinesms2.plugin
		then:
			plugin == "radio"
	}
}
