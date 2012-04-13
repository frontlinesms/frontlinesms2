package frontlinesms2

class Announcement extends Activity {
	static constraints = {
		messages(nullable:false)
	}
	
	static def getType() {
		return 'announcement'
	}
}
