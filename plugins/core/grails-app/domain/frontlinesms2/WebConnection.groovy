package frontlinesms2

import org.apache.camel.*
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class WebConnection extends Activity {
	def camelContext
	def webConnectionService
	enum HttpMethod { POST, GET }
	static String getShortName() { 'webConnection' }

	// Substitution variables
	static subFields = ['message_body' : { msg -> msg.text},
		'message_src_number' : { msg -> msg.src },
		'message_src_name' : { msg -> Contact.findByMobile(msg.src)?.name ?: msg.src },
		'message_timestamp' : { msg -> msg.dateCreated }]
	
	/// Variables
	String url
	HttpMethod httpMethod
	static hasMany = [requestParameters:RequestParameter]
	static hasOne = [keyword: Keyword]
	
	static constraints = {}

	def processKeyword(Fmessage message, Boolean exactMatch) {
		this.addToMessages(message)
		this.save(failOnError:true)
		webConnectionService.send(message)
	}

	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:activity-webconnection-${WebConnection.this.id}")
						.beanRef('webConnectionService', 'preProcess')
						.setHeader(Exchange.HTTP_URI,
								simple('${header.url}'))
						.to(url)
						.beanRef('webConnectionService', 'postProcess')
						.routeId("activity-webconnection-${WebConnection.this.id}")]
			}
		}.routeDefinitions
	}

	def activate(){
		println "*** ACTIVATING ACTIVITY ***"
		try {
			def routes = this.routeDefinitions
			camelContext.addRouteDefinitions(routes)
			println "################# Activating WebConnection :: ${this}"
			LogEntry.log("Created WebConnection routes: ${routes*.id}")
		} catch(FailedToCreateProducerException ex) {
			logFail(this, ex.cause)
		} catch(Exception ex) {
			logFail(this, ex)
			camelContext.stopRoute("activity-webconnection-${this.id}")
			camelContext.removeRoute("activity-webconnection-${this.id}")
		}
	}

	def deactivate(){
		println "################ Deactivating WebConnection :: ${this}"
		camelContext.stopRoute("activity-webconnection-${this.id}")
		camelContext.removeRoute("activity-webconnection-${this.id}")
	}
}
	