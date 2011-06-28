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
	
	def "csv file is generated from provided list of messages"() {
		given:
			def gMessage = new Fmessage(src:'gerad', text:'i love pie')
			def gMessage2 = new Fmessage(src:'steve', text:'i hate beef')
			mockDomain(Fmessage, [gMessage, gMessage2])
		when:
			controller.generateCSVReport([gMessage, gMessage2])
			def csv = '''"DatabaseID","Source","Destination","Text","Date"
"''' + gMessage.id + '''","gerad","null","i love pie","null"
"''' + gMessage2.id + '''","steve","null","i hate beef","null"'''
		then:
			controller.renderArgs == [contentType:"text/csv", text:csv, encoding:"UTF-8"]
	}
	
	def "pdf file is generated from provided list of messages"() {
		given:
			def gMessage = new Fmessage(src:'gerad', text:'i love pie')
			def gMessage2 = new Fmessage(src:'steve', text:'i hate beef')
			mockDomain(Fmessage, [gMessage, gMessage2])
		when:
			def model = controller.generatePDFReport([gMessage, gMessage2])
		then:
			model == [gMessage, gMessage2]
	}
}

