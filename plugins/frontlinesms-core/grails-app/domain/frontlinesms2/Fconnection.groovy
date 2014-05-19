package frontlinesms2

import grails.util.Environment

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.apache.camel.Exchange
import org.codehaus.groovy.grails.commons.ApplicationHolder

// Please don't instantiate this class.  We would make it abstract if it didn't make testing
// difficult, and stop us calling GORM queries across all subclasses.
class Fconnection {
	def fconnectionService
	def dispatchRouterService
	def mobileNumberUtilService
	
	static final String HEADER_FCONNECTION_ID = 'fconnection-id'
	static transients = ['status', 'routeDefinitions']
	static String getShortName() { 'base' }
	
	static final implementations = [FrontlinesyncFconnection,
			SmslibFconnection,
			ClickatellFconnection,
			IntelliSmsFconnection,
			NexmoFconnection,
			SmppFconnection]
	static final getImplementations(params) {
		(params.beta || Boolean.parseBoolean(ApplicationHolder.application.mainContext.getBean('appSettingsService').beta ?: 'false')) ? betaImplementations: implementations
	}

	static getBetaImplementations() { [SmssyncFconnection] + implementations }

	static getNonnullableConfigFields = { clazz ->
		def fields = clazz.configFields
		if (fields) {
			if(fields instanceof Map) return fields.getAllValues()?.findAll { field -> !clazz.constraints[field].blank }
			else return fields.findAll { field ->
				if(!(clazz.metaClass.hasProperty(null, field).type in [Boolean, boolean])) {
					!clazz.constraints[field].nullable
				}
			}
		} else {
			return fields
		}
	}

	static mapping = {
		sort id:'asc'
		tablePerHierarchy false
		version false
	}

	static constraints = {
		name blank:false
	}

	String name
	boolean sendEnabled = true
	boolean receiveEnabled = true
	boolean enabled = true

	def getStatus() {
		fconnectionService.getConnectionStatus(this)
	}

	def getMessages() {
		TextMessage.findAllByConnectionId(this.id)
	}

	def addToMessages(msg) {
		msg.connectionId = this.id
		msg.save()
	}

	def getFlagCSSClasses() {
		if('fromNumber' in this.properties) {
			return mobileNumberUtilService.getFlagCSSClasses(fromNumber) 
		} else { 
			return 'flag'
		}
	}

	def updateDispatch(Exchange x) {
		dispatchRouterService.updateDispatch(x, DispatchStatus.SENT)
	}

	def getDisplayMetadata() {
		return null
	}

	boolean isUserMutable() {
		return true
	}

	List<RouteDefinition> getRouteDefinitions() {
		if(Environment.current != Environment.TEST) {
			throw new IllegalStateException("Do not know how to create routes for Fconnection of class: ${this.class}")
		}
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [
					from('seda:nowhere')
							.setHeader(HEADER_FCONNECTION_ID, simple(Fconnection.this.id.toString()))
							.to('bad:fconnection?subclassed=false')
							.routeId("out-${Fconnection.this.id}"),
					from('bad:fconnection?subclassed=false')
							.setHeader(HEADER_FCONNECTION_ID, simple(Fconnection.this.id.toString()))
							.to('stream:out')
							.routeId("in-${Fconnection.this.id}")]
			}
		}.routeDefinitions
	}
}
