package frontlinesms2

class Autoreply extends Activity {
	Keyword keyword
	
	static constraints = {
		name(blank: false, nullable: false, maxSize: 255, unique: true)
		sentMessageText(nullable:false, blank:false)
		keyword(nullable:false)
	}
}
