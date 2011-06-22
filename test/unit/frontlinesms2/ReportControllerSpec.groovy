package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ReportControllerSpec extends ControllerSpec {
	def "generated csv looks right innit"() {
		given:
			def gMessage = new Fmessage(src:'geraldine', text:'hi david u r a hunk')
			mockDomain(Fmessage, [gMessage])
		when:
			controller.create()
			def csv = '''"DatabaseID","Source","Destination","Text","Date"
"''' + gMessage.id + '''","geraldine","null","hi david u r a hunk","null"'''
		then:
			controller.renderArgs == [contentType:"text/csv", text:csv, encoding:"UTF-8"]
	}
}

