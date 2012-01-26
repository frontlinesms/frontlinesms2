package frontlinesms2

class Announcement extends Activity {
	String type = 'announcement'
	
	static constraints = {
		messages(nullable:false)
	}
}
