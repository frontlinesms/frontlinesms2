package frontlinesms2

class ExternalCommand extends Activty{
	String url
	String sendMethod
	static hasOne = [keyword: Keyword]

	static constraints = {}
}