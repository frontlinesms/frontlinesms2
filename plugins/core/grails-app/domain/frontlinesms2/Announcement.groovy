package frontlinesms2

class Announcement extends Activity {
	static boolean isEditable = { false }
	static String getShortName() { 'announcement' }

	static mapping = {
		version false
	}

	static constraints = {
		messages(nullable:false)
	}
}

