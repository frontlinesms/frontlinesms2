package frontlinesms2

class Announcement extends Activity {
	static boolean isEditable = { false }
	static String getShortName() { 'announcement' }

	static mapping = {
		version false
	}

	static constraints = {
		name(blank:false, nullable:false, validator: { val, obj ->
			def similarName = Announcement.findByNameIlike(val)
			if(!(similarName?.deleted || similarName?.archived))
				return !similarName || obj.id == similarName.id
			})
		messages(nullable:false)
	}
}

