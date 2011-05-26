package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ReportControllerSpec extends ControllerSpec {
	def "generated csv looks right innit"() {
		given:
			mockDomain(Fmessage, [new Fmessage(src:'geraldine', text:'hi david u r a hunk')])
		when:
			controller.create()
			println "renderArgs: ${controller.renderArgs}"
			println "response: ${controller.response}"
		then:
			controller.renderArgs == [contentType:"text/csv", text:'''"DatabaseID","Source","Destination","Text","Date"
"1","geraldine","null","hi david u r a hunk","null"''', encoding:"UTF-8"]
	}
}

