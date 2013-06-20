package frontlinesms2

class SystemNotification {
	private static final int MAX_TEXT_LENGTH = 511
	String text
	boolean read
	String topic
	
	static constraints = {
		text(blank:false, maxSize:MAX_TEXT_LENGTH)
		topic(nullable:true)
	}

	public void setText(String text) {
		this.text = text?.truncate(MAX_TEXT_LENGTH)
	}
}

