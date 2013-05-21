package grails.plugin.geb

import grails.plugin.remotecontrol.RemoteControl
import groovy.sql.Sql

/** TODO you should directly extend geb.spock.GebReportingSpec instead of this class */
@Deprecated
class GebSpec extends geb.spock.GebReportingSpec {
	static final remoteControl
	static {
		remoteControl = new RemoteControl()
	}
	static remote(Closure c) { remoteControl.exec(c) }

	def cleanupSpec() {
		remote {
			// CLearing the hibernate session should improve performance of tests over time
			frontlinesms2.Contact.withSession { s ->
				s.clear()
			}
			null
		}
	}

	def cleanup() {
		remote {
			def appInstanceId = System.properties['frontlinesms.appInstanceId']
			if(!appInstanceId) appInstanceId = "${new Random().nextLong()}"
			System.properties['frontlinesms.appInstanceId'] = appInstanceId

			def sql = Sql.newInstance("jdbc:h2:mem:testDb$appInstanceId", 'sa', '', 'org.h2.Driver')
			sql.execute "SET REFERENTIAL_INTEGRITY FALSE"
			sql.eachRow("SHOW TABLES") { table -> sql.execute('DELETE FROM ' + table.TABLE_NAME) } 
			sql.execute "SET REFERENTIAL_INTEGRITY TRUE"
			null
		}
	}
}

