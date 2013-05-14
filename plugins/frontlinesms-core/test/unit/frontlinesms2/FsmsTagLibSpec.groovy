package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(FsmsTagLib)
@Mock([Folder])
class FsmsTagLibSpec extends Specification {
	def "fieldErrors should return the errors of a domain or command object" () {
		given:
			def folder = new Folder(name:"")
		when:
			folder.validate()
		then:
			applyTemplate('<fsms:fieldErrors bean="${bean}" field="name"/>', [bean:folder]) == "<label for='name' generated='true' class='error'>Property [name] of class [class frontlinesms2.Folder] cannot be blank</label>"
	}
}
