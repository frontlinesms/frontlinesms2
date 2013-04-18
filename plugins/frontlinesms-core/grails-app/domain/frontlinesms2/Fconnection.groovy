package frontlinesms2

import grails.util.Environment

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

// Please don't instantiate this class.  We would make it abstract if it didn't make testing
// difficult, and stop us calling GORM queries across all subclasses.
class Fconnection {
	def fconnectionService
	
	static final String HEADER_FCONNECTION_ID = 'fconnection-id'
	static transients = ['status', 'routeDefinitions']
	static String getShortName() { 'base' }

	static hasMany = [messages: Fmessage]
	
	static final def implementations = [SmslibFconnection,
			ClickatellFconnection,
			IntelliSmsFconnection,
			NexmoFconnection,
			SmssyncFconnection,
			SmppFconnection]

	static getNonnullableConfigFields = { clazz ->
		def fields = clazz.configFields
		if (fields) {
			if(fields instanceof Map) return fields.getAllValues()?.findAll { field -> !clazz.constraints[field].blank }
			else return fields.findAll { field ->
				"beep".execute()
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
	}

	static constraints = {
		name blank:false
	}
	
	String name
	boolean sendEnabled = true
	boolean receiveEnabled = true
	boolean enabled = true
	
	static namedQueries = {
		findByMessages { messageInstance ->
				messages {
					eq 'id', messageInstance.id
				}
		}
	}

	def getStatus() {
		fconnectionService.getConnectionStatus(this)
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
