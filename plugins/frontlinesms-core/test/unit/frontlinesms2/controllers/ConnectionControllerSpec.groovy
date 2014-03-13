package frontlinesms2.controllers

import frontlinesms2.*
import org.codehaus.groovy.grails.commons.ApplicationHolder
import spock.lang.*

@TestFor(ConnectionController)
@Mock([Fconnection, FconnectionService, LogEntry, SmslibFconnection, SystemNotification])
class ConnectionControllerSpec extends Specification {
	def appSettingsService

	def setup() {
		appSettingsService = Mock(AppSettingsService)
		controller.appSettingsService = appSettingsService
		controller.grailsApplication = [mainContext: [:]]
		controller.metaClass.setFlashMessage = { String msg -> msg }
		ApplicationHolder.metaClass.static.getApplication = { -> return [mainContext: [getBean: { String name -> [beta:'true'] }] ] }
	}

	def 'createRoute should trigger EnableFconnectionJob'() {
		setup:
			def routesTriggered = []
			EnableFconnectionJob.metaClass.static.triggerNow = { LinkedHashMap map -> 
				routesTriggered << map.connectionId
			}
			def connection = buildTestConnection()
		when:
			params.id = connection.id // mock the parameters for the request.  NB. mockParams cannot be overridden - only added and removed from
			controller.enable()
		then:
			routesTriggered == [connection.id]
	}

	def "delete should delete an inactive Fconnection"() {
		given:
			def c = buildTestConnection(ConnectionStatus.DISABLED)
			params.id = c.id
		when:
			controller.delete()
		then:
			notThrown(RuntimeException)
			SmslibFconnection.findAll() == []
	}

	@Unroll
	def 'delete should throw an exception for an active fconnection'() {
		given:
			def c = buildTestConnection(status)
			params.id = c.id
		when:
			controller.delete()
		then:
			thrown(RuntimeException)
			Fconnection.findAll() == [c]
		where:
			status << [ConnectionStatus.CONNECTED, ConnectionStatus.NOT_CONNECTED,
					ConnectionStatus.CONNECTING, ConnectionStatus.FAILED]
	}

	def 'can set the routing preferences'() {
		given:
			params.routingUseOrder = "uselastreceiver"
		when:
			controller.changeRoutingPreferences()
		then:
			1 * appSettingsService.set('routing.use','uselastreceiver')
			0 * appSettingsService.set(_, _)
	}

	def "can set routing rules available connections"() {
		given:
			params.routingUseOrder = "uselastreceiver,fconnection-1,fconnection-3,fconnection-5"
		when:
			controller.changeRoutingPreferences()
		then:
			1 * appSettingsService.set('routing.use','uselastreceiver,fconnection-1,fconnection-3,fconnection-5')
	}

	def "can retrieve routing rules defined for connections with send enabled"() {
		given:
			def conn1 = new SmslibFconnection(name:"Huawei Modem", port:'/dev/cu.HUAWEIMobile-Modem', baud:9600, pin:'1234').save(failOnError:true)
			def conn2 = new SmslibFconnection(name:"COM4", port:'COM4', baud:9600).save(failOnError:true)
			new SmslibFconnection(name:"COM5", port:'COM5', baud:9600, sendEnabled:false).save(failOnError:true)
			controller.appSettingsService = ['routing.use':"uselastreceiver,fconnection-${conn2.id},fconnection-${conn1.id}"]
		when:
			def model = controller.list()
		then:
			model.fconnectionRoutingMap*.key*.toString() == ["uselastreceiver", conn2, conn1]*.toString()
			model.fconnectionRoutingMap*.value == [true,true,true]
	}

	def "should not display routing rules for devices that have been deleted from the system"() {
		given:
			def conn1 = new SmslibFconnection(name:"Modem", port:'/dev/cu.HUAWEIMobile-Modem', baud:9600, pin:'1234').save(failOnError:true)
			def conn2 = new SmslibFconnection(name:"COM5", port:'COM4', baud:9600).save(failOnError:true)
			controller.appSettingsService = ['routing.use':"uselastreceiver,fconnection-${conn2.id},fconnection-3,fconnection-${conn1.id}"]
		when:
			def model = controller.list()
		then:
			model.fconnectionRoutingMap*.key*.toString() == ['uselastreceiver', conn2, conn1]*.toString()
	}

	@Unroll
	def 'creating a connection should add it to the enabled routes list'() {
		given: 'TODO set up request vars'
			def ass = ['routing.use':initialSetting]
			controller.appSettingsService = ass
			params.connectionType = 'smssync'
			params.smssyncname = 'test-connection'
			params.smssyncreceiveEnabled = true
			params.smssyncsecret = ''
			params.smssyncsendEnabled = true
			params.smssynctimeout = '60'
		when: 'new connection is saved'
			controller.save()
		then: 'the is enabled at the end of the enabled connections list'
			ass['routing.use'] ==~ finalSetting
		where:
			initialSetting | finalSetting
			null|/^fconnection-\d+$/
			''|/^fconnection-\d+$/
			'fconnection-C'|/^fconnection-C,fconnection-\d+$/
			'fconnection-C,fconnection-A'|/^fconnection-C,fconnection-A,fconnection-\d+$/
	}

	private def buildTestConnection(status) {
		def c = new Fconnection(name:'test').save(failOnError:true)
		def fconnectionService = Mock(FconnectionService)
		fconnectionService.getConnectionStatus(_) >> { connection -> status }
		c.fconnectionService = fconnectionService
		assert c.fconnectionService != null
		return c
	}
}

