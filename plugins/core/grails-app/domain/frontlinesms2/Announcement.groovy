package frontlinesms2

class Announcement extends Activity {

	static constraints = {
		messages(nullable:false)
	}
	
	def getType() {
		return 'announcement'
	}
}
