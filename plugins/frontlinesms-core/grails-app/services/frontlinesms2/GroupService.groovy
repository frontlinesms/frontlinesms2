package frontlinesms2

class GroupService {
	def delete(Group g) {
		GroupMembership.deleteFor(g)
		try {
			g.delete(flush:true)
			return true
		} catch(Exception ex) {
			ex.printStackTrace()
			return false
		}
	}
}

