package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class FsmsTagLibSpec extends GroovyPagesSpec {
	def "confirmTypeRow generates a row that is properly internationalized"() {
		setup:
			def clazz = new TestFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTypeRow instanceClass="${clazz}" />'
		then:
			output.contains '<tr><td class="field-label">Type</td><td id="confirm-type"></td></tr>'
	}
	
	def "confirmTable returns a table of values"() {
		setup:
			def clazz = new TestFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTable instanceClass="${clazz}"/>'
		then:
			output == '<table id="smslib-confirm"><tr><td class="field-label">Type</td><td id="confirm-type"></td></tr><tr><td class="field-label">Name</td><td id="confirm-name"></td></tr></table>'
	}
	
	def "INPUTS should generate input fields for all configFields"() {
		setup:
			def clazz = new SmslibFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			clazz.configFields.every {type -> output.contains "name=\"${clazz.shortName + type}\"" }
	}
	
	def "INPUT creates a textfield for a string field"() {
		setup:
			def clazz = new SmslibFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:input field="name" instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains '<input type="text" field="name" name="smslibname" value="" id="smslibname" />'
	}
	
	def "INPUT creates a password input for a string field"() {
		setup:
			def clazz = new SmslibFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:input field="pin" instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains '<input type="password" field="pin" name="smslibpin" value="" id="smslibpin" />'
	}
	
	def "INPUT creates a select dropdown for an enum object"() {
		setup:
			def clazz = new EmailFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:input field="receiveProtocol" instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains '<select name="emailreceiveProtocol" field="receiveProtocol" id="emailreceiveProtocol" >'
			output.contains '</select>'
	}
	
	def "INPUT creates a checkbox for boolean fields"() {
		setup:
			def clazz = new IntelliSmsFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:input field="send" instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains('<input type="checkbox" name="intellismssend" field="send" id="intellismssend"  />')
	}
	
	def "INPUTS generates subsections for a field Map"() {
		setup:
			def clazz = new IntelliSmsFconnection(name:"Test", send:true, username:"test_acc", password:"test")
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
			def configFields = clazz.configFields
		then:
			configFields.each {k,v -> if(v) output.contains("<div id=\"$k-subsection\">")}
			clazz.configFields.send.each { output.contains "field=\"$it\" class=\"$it-subsection-member\""}
			clazz.configFields.receive.each { output.contains "field=\"$it\" class=\"$it-subsection-member\""}
	}
	
	def "confirmTable generates all the details of a CrazyFconnection"() {
		setup:
			def clazz = new CrazyFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTable instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			clazz.configFields.each { k,v -> 
				output.contains("confirm-$k")
				v?.each { output.contains "confirm-$it"}
			}
	}
	
	def "confirmTable generates all the details of an intellismsFconnection"() {
		setup:
			def clazz = new IntelliSmsFconnection(name:"Test", send:true, username:"test_acc", password:"test")
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTable instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			clazz.configFields.each { k,v -> 
				output.contains("confirm-$k")
				v?.each { output.contains "confirm-$it"}
			}
	}
	
	def "INPUTS generates subsections for a CrazyFconnection"() {
		setup:
			def clazz = new CrazyFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
			def configFields = clazz.configFields
		then:
			configFields.each {k,v -> if(v) output.contains("<div id=\"$k-subsection\">")}
			clazz.configFields.mobicash.each {k,v -> output.contains "field=\"$k\" class=\"$k-subsection-member\""}
			clazz.configFields.mobicash.bank.each { output.contains "field=\"$it\" class=\"$it-subsection-member\""}
	}
	
}

class TestFconnection {
	String name
	static configFields = ["name"]
	String simpleName = "smslibfconnection"
	static String getShortName() { 'smslib' }
}

class CrazyFconnection {
	static configFields = [
			squid:null,
			mobicash:[
						bank:['accountNumber', 'sortCode'],
						handphone:['network', 'emailReceiveProtocol']]]
	static typeFields = ["mobicash", "bank", "handphone"]
	static String getShortName() { 'crazy' }
	boolean squid
	String simpleName = "smslibfconnection"
	String name
	String bank
	String handphone
	String accountNumber
	String sortCode
	String network
	String emailReceiveProtocol
	
	boolean mobicash
}


