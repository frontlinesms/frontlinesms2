package frontlinesms2

class Announcement extends Activity {
	static boolean isEditable = { false }
	static String getShortName() { 'announcement' }
	static constraints = {
		messages(nullable:false)
	}
}
