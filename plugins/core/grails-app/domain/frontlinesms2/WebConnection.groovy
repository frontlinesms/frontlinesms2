package frontlinesms2

class WebConnection extends Activity {

	static String getShortName() { 'webConnection' }

	/// Variables
	static hasOne = [keyword: Keyword, connection: HttpWebConnectionFconnection]
	
	static constraints = {}

}
	