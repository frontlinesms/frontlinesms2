package frontlinesms2

class Announcement extends Activity {
	static boolean isEditable = { false }
	static String getShortName() { 'announcement' }

	static mapping = {
		version false
	}

	static constraints = {
		name(blank:false, nullable:false, validator: { val, obj ->
			if(obj?.deleted || obj?.archived) return true
			def identical = Announcement.findAllByNameIlike(val)
			if(!identical) return true
			else if (identical.any { it != obj && !it?.archived && !it?.deleted }) return false
			else return true
			})
		messages(nullable:false)
	}
}

