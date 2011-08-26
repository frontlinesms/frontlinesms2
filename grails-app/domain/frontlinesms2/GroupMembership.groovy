package frontlinesms2

import org.apache.commons.lang.builder.HashCodeBuilder

class GroupMembership implements Serializable {
    Contact contact
    Group group

    boolean equals(other) {
        if (!(other instanceof GroupMembership)) {
            return false
        }

        return other.contact.id == contact.id && other.group.id == group.id
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
//		GroupMembership.delete
		executeUpdate("DELETE FROM GroupMembership WHERE contact=:contact", [contact: c])
	}

	static void deleteFor(Group g) {
		executeUpdate("DELETE FROM GroupMembership WHERE group=:group", [group: g])
	}

   static mapping = {
      id composite: ['group', 'contact']
      version false
      table 'group_member'
   }
}
