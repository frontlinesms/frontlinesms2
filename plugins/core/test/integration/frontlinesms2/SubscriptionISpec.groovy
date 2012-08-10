package frontlinesms2

class SubscriptionISpec {
	def 'triggering join keyword for an existing contact should add him to a group'() {}
	def 'triggering leave keyword for an existing contact should remove him from a group'() {}
	def 'triggering join keyword for a non-existing contact should create him and add him to the group'() {}
	def 'triggering leave keyword for a non-existing contact should have no effect'() {}
	def 'triggering toggle for a non-existing contact should create him and add him to the group'() {}
	def 'triggering toggle for an existing contact should add him to a group if he is already a member'() {}
	def 'triggering toggle for an existing contact should remove him from a group if he is already a member'() {}
	def 'exact matches should map to alias'() {}
	def 'non-exact matches should map to alias'() {}
	def 'exact match without alias match should map to blank setting'() {}
	def 'non-exact match without alias should not map'() {}
}

