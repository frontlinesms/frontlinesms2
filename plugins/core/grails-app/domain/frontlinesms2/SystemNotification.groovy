package frontlinesms2

class SystemNotification {
	String text
	boolean read
	
	static constraints = {
		text(blank:false, nullable:false, unique: true)
	}
}
