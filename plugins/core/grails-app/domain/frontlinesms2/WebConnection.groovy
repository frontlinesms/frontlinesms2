package frontlinesms2

class WebConnection extends Activity {
	enum HttpMethod { POST, GET }
	static String getShortName() { 'webConnection' }

	// Substitution variables
	public static final String MESSAGE_BODY = "\${MESSAGE_BODY}"
	public static final String MESSAGE_SOURCE_NUMBER = "\${MESSAGE_SRC_NUMBER}"
	public static final String MESSAGE_SOURCE_NAME = "\${MESSAGE_SRC_NAME}"
	public static final String MESSAGE_TIMESTAMP = "\${MESSAGE_TIMESTAMP}"
	
	/// Variables
	String url
	HttpMethod httpMethod
	static hasMany = [requestParameters:RequestParameter]
	static hasOne = [keyword: Keyword]
	
	static constraints = {}
	def processKeyWord(Fmessage message, Boolean exactMatch){}
	def send(Fmessage message){}
}
	