package frontlinesms2

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import grails.plugin.geb.GebSpec

class PhonesAndConnectionsSettingsSpec extends grails.plugin.geb.GebSpec {
	
	def 'add new connection option is available in connection settings panel'() {
		when:
			to ConnectionsListPage	
		then:
			btnNewConnection.text() == "Add new connection"
			assert btnNewConnection.children().getAttribute("href") == "/frontlinesms2/connection/create"
	}
	

	def 'connections are listed in "phone & connections" panel'() {
		given:
			createTestConnections()
		when:
			to ConnectionsListPage
		then:
			println "Connections: ${Fconnection.findAll()}"
			lstConnections != null
			println "----"
			println lstConnections
			println lstConnections.children()
			lstConnections.children().collect() {
				it.text()
			} == ["'MTN Dongle' (Phone/Modem)", "'David's Clickatell account' (Clickatell SMS Gateway)", "'Miriam's Clickatell account' (Clickatell SMS Gateway)"]
		cleanup:	
			deleteTestConnections()
	}

	def createTestConnections() {
		[new Fconnection(name:'MTN Dongle', type:'Phone/Modem', camelAddress:'1'),
				new Fconnection(name:'David\'s Clickatell account', type:'Clickatell SMS Gateway', camelAddress:'2'),
				new Fconnection(name:'Miriam\'s Clickatell account', type:'Clickatell SMS Gateway', camelAddress:'3')].each() {
			it.save(flush:true, failOnError: true)
		}
	}

	def deleteTestConnections() {
		Fconnection.findAll().each() { it.delete(flush: true) }
	}
}


