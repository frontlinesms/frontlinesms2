package grails.plugin.geb

/** TODO you should directly extend geb.spock.GebReportingSpec instead of this class */
@Deprecated
class GebSpec extends geb.spock.GebReportingSpec {
	def cleanupSpec() {
		// CLearing the hibernate session should improve performance of tests over time
		frontlinesms2.Contact.withSession { s ->
			s.clear()
		}
	}
}

