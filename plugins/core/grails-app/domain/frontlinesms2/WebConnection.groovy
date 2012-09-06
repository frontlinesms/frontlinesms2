package frontlinesms2

import org.apache.camel.*
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class WebConnection extends Activity {
	def camelContext
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
	def processKeyWord(Fmessage message, Boolean exactMatch){
		this.addToMessages(message)
		this.save(failOnError:true)
		send(message)
	}
	def send(Fmessage message){
		def headers = [:]
		headers.'frontlinesms.fmessageId' = message.id
		headers.'frontlinesms.webConnectionId' = this.id
		sendMessageAndHeaders('seda:dispatches', it, headers)
	}

	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:out-webconnection-${this.id}")
						.beanRef('webConnectionService', 'preProcess')
						.setHeader(Exchange.HTTP_URI,
								simple('${header.url}'))
						.to('${header.url}')
						.beanRef('webConnectionService', 'postProcess')
						.routeId("out-webconnection-${this.id}")]
			}
		}.routeDefinitions
	}

	def activate(){
		try {
			def routes = this.routeDefinitions
			camelContext.addRouteDefinitions(routes)
			println "################# Activating WebConnection :: ${this}"
			LogEntry.log("Created WebConnection routes: ${routes*.id}")
		} catch(FailedToCreateProducerException ex) {
			logFail(this, ex.cause)
		} catch(Exception ex) {
			logFail(this, ex)
			camelContext.stopRoute("out-webconnection-${this.id}")
			camelContext.removeRoute("out-webconnection-${this.id}")
		}
	}

	def deactivate(){
		println "################ Deactivating WebConnection :: ${this}"
		camelContext.stopRoute("out-webconnection-${this.id}")
		camelContext.removeRoute("out-webconnection-${this.id}")
	}
}
	