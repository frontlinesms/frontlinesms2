package frontlinesms2.connection

class ConnectionListSpec extends ConnectionGebSpec {
	def 'When there are no connections, this is explained to the user'() {
		when:
			to ConnectionListPage
		then:
			lstConnections.tag() == 'div'
			lstConnections.text() == 'You have no connections configured.'
	}
	
	def 'There is a New Connection button available when there are no connections'() {
		when:
			to ConnectionListPage
		then:
			btnNewConnection != null
			btnNewConnection.text() == "Add new connection"
	}
}
