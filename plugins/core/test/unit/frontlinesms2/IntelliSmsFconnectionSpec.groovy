package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(IntelliSmsFconnection)
class IntelliSmsFconnectionSpec extends Specification {
	def 'creating a sendOnly IntelliSmsFconnection validates'() {
		when:"send property not set"
			def intellismsConn = new IntelliSmsFconnection(name:"test", username:"test", password:"****")
		then:
			!intellismsConn.validate()
		when:"username and password not set"
			intellismsConn = new IntelliSmsFconnection(name:"test", send:true)
		then:
			!intellismsConn.save()
			intellismsConn.hasErrors()
		when:
			intellismsConn = new IntelliSmsFconnection(send:true, name:"test", username:"test", password:"****")
		then:
			intellismsConn.save()
	}
	
	def 'creating a receiveOnly IntelliSmsFconnection validates'() {
		when:"receive property not set"
			def intellismsConn = new IntelliSmsFconnection(name:"test", serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP)
		then:
			!intellismsConn.validate()
		when:"email fields not set"
			intellismsConn = new IntelliSmsFconnection(receive:true, name:"test", username:"test", password:"****")
		then:
			!intellismsConn.save()
			intellismsConn.hasErrors()
		when:
			intellismsConn =  new IntelliSmsFconnection(name:"test", receive:true, serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP)
		then:
			intellismsConn.save()
	}
	
	def 'creating a send and receive IntelliSmsFconnection validates'() {
		when:"receive property not set"
			def intellismsConn = new IntelliSmsFconnection(receive: true, send:true) 
		then:
			!intellismsConn.validate()
		when:"email fields not set"
			intellismsConn = new IntelliSmsFconnection(receive:true, name:"test", username:"test", password:"****")
		then:
			!intellismsConn.save()
			intellismsConn.hasErrors()
		when:
			intellismsConn =  new IntelliSmsFconnection(name:"test", receive:true, serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP, send:true, password:"***")
		then:
			!intellismsConn.save()
		when:
			intellismsConn =  new IntelliSmsFconnection(name:"test", receive:true, serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP, send:true, username:"test", password:"***")
		then:
			intellismsConn.save()
	}
	
}
