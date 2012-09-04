package frontlinesms2

class WebConnection extends Activity {
	enum HttpMethod { POST, GET }
	static String getShortName() { 'webConnection' }

	// Substitution variables
	static subFields = ['message_body', 'message_src_number', 'message_src_name', 'message_timestamp']
	
	/// Variables
	String url
	HttpMethod httpMethod
	static hasMany = [requestParameters:RequestParameter]
	static hasOne = [keyword: Keyword]
	
	static constraints = {}
	def processKeyWord(Fmessage message, Boolean exactMatch){}
	def send(Fmessage message){}

	def preProcess(message){}
	def postProcess(exchange){}
}
	