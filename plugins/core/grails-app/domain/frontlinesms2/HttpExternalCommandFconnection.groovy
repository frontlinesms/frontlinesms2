package frontlinesms2

import frontlinesms2.camel.intellisms.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class HttpExternalCommandFconnection extends Fconnection {
	enum HttpMethod { POST, GET }

	// Substitution variables
	public static final String MESSAGE_BODY = "\${MESSAGE_BODY}"
	public static final String MESSAGE_SOURCE_NUMBER = "\${MESSAGE_SRC_NUMBER}"
	public static final String MESSAGE_SOURCE_NAME = "\${MESSAGE_SRC_NAME}"
	public static final String MESSAGE_TIMESTAMP = "\${MESSAGE_TIMESTAMP}"

	String url
	HttpMethod httpMethod
	static Fconnection.ConnectionRole connectionRole = Fconnection.ConnectionRole.FSMS_EXT_COMMAND
	static hasMany = [requestParameters:RequestParameter]
	static belongsTo = [externalCommand: ExternalCommand]
}
