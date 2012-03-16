package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class ImportControllerSpec extends ControllerSpec {
	@Unroll
	def 'getGroupNames should parse v1 CSV groups into v2 groups'() {
		expect:
			controller.getGroupNames(csvValue).sort() == expectedGroupNames
		where:
			csvValue | expectedGroupNames
			'simple' | ['simple']
			'/simpleWithSlash' | ['simpleWithSlash']
			'group 1' | ['group 1']
			'group 1\\group 2' | ['group 1', 'group 2']
			'group 1\\group 2\\group 3' | ['group 1', 'group 2', 'group 3']
			'billy/kid' | ['billy', 'billy-kid', 'kid']
			'billy/kid\\coney' | ['billy', 'billy-kid', 'coney', 'kid']
			'billy/kid/sheep' | ['billy', 'billy-kid', 'billy-kid-sheep', 'kid', 'sheep']
			'/isIt\\/ToDo/Work/jobo' | ['ToDo', 'ToDo-Work', 'ToDo-Work-jobo', 'Work', 'isIt', 'jobo']
	}
	
	@Unroll
	def 'getGroups should return all existing groups and create non-existing ones'() {
		setup:
			mockDomain(Group)
			existingGroupNames.each { new Group(name:it).save(failOnError:true) }
			def createdGroups = controller.getGroups(groupNames)
		expect:
			createdGroups*.name == groupNames
			createdGroups.every { g -> (!(g.name in existingGroupNames)) || g.id }
		where:
			groupNames | existingGroupNames
			['simple'] | []
			['group 1', 'group 2'] | []
			['group 1', 'group 2'] | ['group 1']
			['group 1', 'group 2'] | ['group 2']
			['group 1', 'group 2'] | ['group 1', 'group 2']
	}
}

