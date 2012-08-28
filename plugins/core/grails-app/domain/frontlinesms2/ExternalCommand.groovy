package frontlinesms2

class ExternalCommand extends Activity {

	static String getShortName() { 'externalCommand' }

	// Substitution variables
	public static final String MESSAGE_BODY = "\${MESSAGE_BODY}"
	public static final String MESSAGE_SOURCE_NUMBER = "\${MESSAGE_SRC_NUMBER}"
	public static final String MESSAGE_SOURCE_NAME = "\${MESSAGE_SRC_NAME}"
	public static final String MESSAGE_TIMESTAMP = "\${MESSAGE_TIMESTAMP}"

	/// Variables
	static hasOne = [keyword: Keyword, connection: HttpExternalCommandFconnection]
	
	static constraints = {}

}
	