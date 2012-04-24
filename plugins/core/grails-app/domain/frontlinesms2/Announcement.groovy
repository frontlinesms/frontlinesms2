package frontlinesms2

class Announcement extends Activity {
	static String getShortName() { 'announcement' }
	static constraints = {
		messages(nullable:false)
	}
}
