package frontlinesms2

class ExternalCommand extends Activity{
	String url
	String sendMethod
	static hasOne = [keyword: Keyword]

	static constraints = {}
}