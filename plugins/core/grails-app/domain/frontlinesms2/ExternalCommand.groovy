package frontlinesms2

class ExternalCommand extends Activity {

	// HTTP Request methods
	enum RequestType { POST, GET }

	// Substitution variables
	public static final String MESSAGE_BODY = "\${MESSAGE_BODY}"
	public static final String MESSAGE_SOURCE_NUMBER = "\${MESSAGE_SRC_NUMBER}"
	public static final String MESSAGE_SOURCE_NAME = "\${MESSAGE_SRC_NAME}"
	public static final String MESSAGE_TIMESTAMP = "\${MESSAGE_TIMESTAMP}"

	/// Variables
	String url
	String sendMethod
	static hasOne = [keyword: Keyword]
	RequestType requestType
	
	static constraints = {}

}
	