package frontlinesms2

import frontlinesms2.camel.intellisms.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class HttpExternalCommandFconnection extends Fconnection {
	enum HttpMethod { POST, GET }

	String url
	HttpMethod httpMethod
	static hasMany = [requestParameters:RequestParameter]
}
