package frontlinesms2.controllers

import frontlinesms2.*

import spock.lang.*
import grails.test.mixin.*

@TestFor(ImportController)
@Mock([Group])
class ImportControllerSpec extends Specification {
	@Unroll
	def 'getGroupNames should parse v1 CSV groups into v2 groups'() {
		expect:
			controller.getGroupNames(csvValue).sort() == expectedGroupNames
		where:
			csvValue                    | expectedGroupNames
			'simple'                    | ['simple']
			'/simpleWithSlash'          | ['simpleWithSlash']
			'group 1'                   | ['group 1']
			'group 1\\group 2'          | ['group 1', 'group 2']
			'group 1\\group 2\\group 3' | ['group 1', 'group 2', 'group 3']
			'billy/kid'                 | ['billy', 'billy-kid', 'kid']
			'billy/kid\\coney'          | ['billy', 'billy-kid', 'coney', 'kid']
			'billy/kid/sheep'           | ['billy', 'billy-kid', 'billy-kid-sheep', 'kid', 'sheep']
			'/isIt\\/ToDo/Work/jobo'    | ['ToDo', 'ToDo-Work', 'ToDo-Work-jobo', 'Work', 'isIt', 'jobo']
	}
	
	@Unroll
	def 'getGroups should return all existing groups and create non-existing ones'() {
		setup:
			existingGroupNames.each { new Group(name:it).save(failOnError:true) }
			def createdGroups = controller.getGroups(groupNames)
		expect:
			createdGroups*.name == groupNames
			createdGroups.every { g -> (!(g.name in existingGroupNames)) || g.id }
		where:
			groupNames             | existingGroupNames
			['simple']             | []
			['group 1', 'group 2'] | []
			['group 1', 'group 2'] | ['group 1']
			['group 1', 'group 2'] | ['group 2']
			['group 1', 'group 2'] | ['group 1', 'group 2']
	}

	def 'failedContacts should trigger a download for a file named failedContacts.csv'() {
		given:
			controller.params.failedContacts = '''\
			"Name","Mobile Number","Email","Notes","Group(s)","lake","town"
			"Alice","+123456789","","","Friends\\Not Cats","Victoria",""
			"Bob","+198765432","","","Friends\\Not Cats","","Kusumu"
			"Kate","+198730948","","","","",""
			"Bobby","987654321","","","Camping Group\\Football Updates","",""
			"Sam","987654322","","","Camping Group","",""
			"Ron","987654323","","","Football Updates","",""
			'''
			controller.params.format = "csv"
		when:
			controller.failedContacts()
		then:
			
			controller.response.getHeader('Content-disposition') == "attachment; filename=failedContacts.csv"
	}
}

