package frontlinesms2

class SystemNotification {
	private static final int MAX_TEXT_LENGTH = 511
	String text
	boolean read
	
	static constraints = {
		text(blank:false, maxSize:MAX_TEXT_LENGTH)
	}

	public void setText(String text) {
		this.text = text.truncate(MAX_TEXT_LENGTH)
	}
}
