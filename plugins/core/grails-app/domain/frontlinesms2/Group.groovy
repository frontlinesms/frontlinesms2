package frontlinesms2

class Group {
	String name

	static def shortName = 'group'

	static constraints = {
		name(unique: true, nullable: false, blank: false, maxSize: 255)
	}

	static mapping = {
		// 'group' is a SQL keyword, and so automatic mapping of this class to a
		// table does not work.
		table 'grup'
	}

	def beforeDelete = {
		GroupMembership.deleteFor(this)
	}

	def getMembers() {
		// TODO shouldn't have to filter the GroupMemberships manually here
		Contact.findAll("FROM Contact c, GroupMembership m WHERE m.group=? AND m.contact=c ORDER BY c.name", [this]).collect{ it[0] }
	}

	def addToMembers(Contact c) {
		GroupMembership.create(c, this)
	}
	
	def getAddresses() {
		(getMembers()*.mobile) - [null, '']
	}

	static def getGroupLists(def contactIds) {
		// TODO rewrite this as a beautiful HQL query or criteria
		def getSharedGroupList = { Collection groupList ->
			def groupIds = groupList*.id
			def sharedGroupIds = groupIds?.inject(groupIds[0]){ acc, current -> acc.intersect(current) }
			Group.getAll(sharedGroupIds)
		}

		def getNonSharedGroupList = { Collection groupList1, Collection groupList2 ->
			def groupIdList1 = groupList1*.id
			def groupIdList2 = groupList2*.id
			return Group.getAll(groupIdList1 - groupIdList2)
		}

		def groupInstanceList = []
		Contact.getAll(contactIds).each { c ->
			println "Contact: $c.name"
			println "His groups: ${c.groups*.id}"
			groupInstanceList << c.groups
		}
		println "groupInstanceList: $groupInstanceList"
		def sharedGroupInstanceList = getSharedGroupList(groupInstanceList)
		def nonSharedGroupInstanceList = getNonSharedGroupList(Group.findAll(), sharedGroupInstanceList)

		return [shared:sharedGroupInstanceList, nonShared:nonSharedGroupInstanceList]
	}

	static HashMap<String, List<String>> getGroupDetails() {
		Group.list().collectEntries { ["group-$it.id".toString(), [name:it.name,addresses:it.addresses]] }
	}

	static Set<Contact> findAllWithoutMember(Contact c) {
		// FIXME do this with a single select/join... and do it in GroupMembership??
		def allGroups = Group.findAll();
		def cGroups = c.groups
		def without = allGroups
		cGroups.each() { cg ->
			def remove
			allGroups.each() { ag ->
				if(ag.id == cg.id) remove = ag
			}
			if(remove) {
				allGroups.remove(remove)
			}
		}
		without as Set
	}
}

