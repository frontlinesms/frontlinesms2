package frontlinesms2

import org.apache.commons.lang.builder.HashCodeBuilder

class GroupMembership implements Serializable {
	static mapping = {
		id composite: ['group', 'contact']
		version false
		table 'group_member'
	}

	Contact contact
	Group group

	boolean equals(that) {
		that instanceof GroupMembership &&
				that.contact.id == this.contact.id &&
				that.group.id == this.group.id
	}

	int hashCode() {
		return new HashCodeBuilder().append(contact.id).append(group.id).toHashCode()
	}

	static GroupMembership create(Contact contact, Group group, boolean flush=true) {
		new GroupMembership(contact: contact, group: group).save(flush: flush, insert: true)
	}

	static GroupMembership create(Contact contact, Long groupId, boolean flush=true) {
		new GroupMembership(contact: contact, group: groupId).save(flush: flush, insert: true)
	}

	static boolean remove(Contact contact, Group group, boolean flush=false) {
		GroupMembership contactGroup = GroupMembership.findByContactAndGroup(contact, group)
		return contactGroup ? contactGroup.delete(flush: flush) : false
	}

	static void deleteFor(Contact c, boolean flush=false) {
		executeUpdate("DELETE FROM GroupMembership WHERE contact=:contact", [contact: c])
	}

	static void deleteFor(Group g) {
		executeUpdate("DELETE FROM GroupMembership WHERE group=:group", [group: g])
	}
	
	static def searchForContacts(Long groupId, String contactSearchString, max, offset) {
		def groupMembershipsAndContacts = Contact.executeQuery("SELECT c FROM GroupMembership g JOIN g.contact c WHERE g.group.id=:groupId AND lower(c.name) LIKE :contactSearchString",
				[groupId:groupId, contactSearchString:contactSearchString], [max:max, offset:offset])
		return groupMembershipsAndContacts
	}
	
	static def countSearchForContacts(groupId, String contactSearchString) {
		def count = GroupMembership.executeQuery("SELECT count(c) FROM GroupMembership g JOIN g.contact c WHERE g.group.id=:groupId AND lower(c.name) LIKE :contactSearchString",
				[groupId:groupId, contactSearchString:contactSearchString])
		return count[0]
	}
	
	static getMembers(groupInstance, max, offset) {
		GroupMembership.executeQuery("SELECT gm.contact FROM GroupMembership gm WHERE gm.group=:group", [group: groupInstance], [max: max, offset: offset])
	}
	
	static countMembers(groupInstance) {
		GroupMembership.executeQuery("SELECT gm.contact FROM GroupMembership gm WHERE gm.group=:group", [group: groupInstance]).size()
	}
}
