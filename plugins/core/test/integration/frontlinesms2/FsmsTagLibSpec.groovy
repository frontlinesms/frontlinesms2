package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class FsmsTagLibSpec extends GroovyPagesSpec {
	def "confirmTypeRow generates a row that is properly internationalized"() {
		setup:
			def clazz = new SmsLibClass()
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTypeRow instanceClass="${clazz}" />'
		then:
			output == '<tr><td class="bold">Type</td><td id="confirm-type"></td></tr>'
	}
	
	
}

class SmsLibClass {
	String field
	String simpleName = "smslibfconnection"
	
}
