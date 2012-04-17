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
			output == '<tr><td class="bold">Type</td><td id="confirm-type"></td></tr>'
	}
	
	def "confirmTable returns a table of values"() {
		setup:
			def clazz = new TestFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTable instanceClass="${clazz}"/>'
		then:
			output == '<table id="smslib-confirm"><tr><td class="bold">Type</td><td id="confirm-type"></td></tr><tr><td class="bold">Name</td><td id="confirm-name"></td></tr></table>'
	}
	
	def "INPUTS creates a textfield for a string field"() {
		setup:
			def clazz = new SmslibFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains('<input type="text" field="name" name="smslibname" value="" id="smslibname" />')
	}
	
	def "INPUTS creates a password input for a string field"() {
		setup:
			def clazz = new SmslibFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains('<input type="password" field="pin" name="smslibpin" value="" id="smslibpin" />')
	}
	
	def "INPUTS creates a select dropdown for an enum object"() {
		setup:
			def clazz = new EmailFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains('<select name="emailreceiveProtocol" field="receiveProtocol" id="emailreceiveProtocol" >')
			output.contains('<option value="null">- Select -</option>')
			output.contains('<option value="IMAP" >IMAP</option>')
			output.contains('<option value="IMAPS" >IMAPS</option>')
			output.contains('<option value="POP3" >POP3</option>')
			output.contains('<option value="POP3S" >POP3S</option>')
			output.contains('</select>')
	}
	
	def "INPUTS creates a checkbox for boolean fields"() {
		setup:
			def clazz = new IntelliSmsFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains('<input type="checkbox" name="intellismssend" field="send" id="intellismssend"  />')
			output.contains('<input type="checkbox" name="intellismsreceive" field="receive" id="intellismsreceive"  />')
	}
	
	def "INPUTS populates data fields of existing intellisms SEND connection"() {
		setup:
			def clazz = new IntelliSmsFconnection(name:"Test", send:true, username:"test_acc", password:"test")
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains('<input type="checkbox" name="intellismssend" checked="checked" field="send" id="intellismssend"  />')
			output.contains('<input type="text" field="name" name="intellismsname" value="Test" id="intellismsname" />')
			output.contains('<input type="text" field="username" name="intellismsusername" value="test_acc" id="intellismsusername" />')
			output.contains('<input type="password" field="password" name="intellismspassword" value="test" id="intellismspassword" />')
	}
	
	def "INPUTS populates data fields of existing intellisms RECEIVE connection"() {
		setup:
			def clazz =  new IntelliSmsFconnection(name:"Test Intellisms", receive:true, serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP)
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains('<input type="checkbox" name="intellismsreceive" checked="checked" field="receive" id="intellismsreceive"  />')
			output.contains('<input type="text" field="name" name="intellismsname" value="Test Intellisms" id="intellismsname" />')
			output.contains('<input type="text" field="serverName" name="intellismsserverName" value="imap.gmail.com" id="intellismsserverName" />')
			output.contains('<input type="text" field="serverPort" name="intellismsserverPort" value="993" id="intellismsserverPort" />')
			output.contains('<input type="text" field="emailUserName" name="intellismsemailUserName" value="test" id="intellismsemailUserName" />')
			output.contains('<input type="password" field="emailPassword" name="intellismsemailPassword" value="****" id="intellismsemailPassword" />')
	}
	
	def "confirmTable generates all the details of an intellismsFconnection"() {
		setup:
			def clazz = new IntelliSmsFconnection(name:"Test", send:true, username:"test_acc", password:"test")
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTable instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains('<tr><td class="bold">Send Configurations</td><td id="confirm-send"></td></tr>')
			output.contains('<tr><td class="bold">Name</td><td id="confirm-name"></td></tr>')
			output.contains('<tr><td class="bold">Username</td><td id="confirm-username"></td></tr>')
			output.contains('<tr><td class="bold">Password</td><td id="confirm-password"></td></tr>')
			output.contains('<td class="bold">Receive Configurations</td>')
			output.contains('<tr><td class="bold">Server Name</td><td id="confirm-serverName"></td></tr>')
			output.contains('<tr><td class="bold">Server Port</td><td id="confirm-serverPort"></td></tr>')
			output.contains('<tr><td class="bold">Username</td><td id="confirm-username"></td></tr>')
			output.contains('<tr><td class="bold">Password</td><td id="confirm-password"></td></tr>')
	}
	
}

class TestFconnection {
	String name
	static configFields = ["name"]
	String simpleName = "smslibfconnection"
	static String getShortName() { 'smslib' }
}


