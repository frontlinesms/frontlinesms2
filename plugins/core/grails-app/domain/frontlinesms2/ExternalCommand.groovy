package frontlinesms2

class ExternalCommand extends Activity {

	static String getShortName() { 'externalCommand' }

	/// Variables
	static hasOne = [keyword: Keyword, connection: HttpExternalCommandFconnection]
	
	static constraints = {}

}
	