package grails.plugin.geb

import grails.plugin.remotecontrol.RemoteControl

/** TODO you should directly extend geb.spock.GebReportingSpec instead of this class */
@Deprecated
class GebSpec extends geb.spock.GebReportingSpec {
	static final remote
	static final vanillaRemote
	static {
		remote = new RemoteControl()
		remote.useNullIfResultWasUnserializable = true
		vanillaRemote = remote
	}

	def cleanupSpec() {
		// CLearing the hibernate session should improve performance of tests over time
		frontlinesms2.Contact.withSession { s ->
			s.clear()
		}
	}
}

