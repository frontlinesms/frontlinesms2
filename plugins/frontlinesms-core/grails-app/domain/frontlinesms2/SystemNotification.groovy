package frontlinesms2

class SystemNotification {
//> CONSTANTS
	private static final int MAX_TEXT_LENGTH = 511

//> INSTANCE PROPERTIES
	String text
	boolean rd
	String topic

//> DOMAIN SETUP
	static constraints = {
		text blank:false, maxSize:MAX_TEXT_LENGTH
		topic nullable:true
	}

//> ACCESSORS
	public void setText(String text) {
		this.text = text?.truncate(MAX_TEXT_LENGTH)
	}

	public boolean isRead() { return this.rd }
	public boolean setRead(boolean read) { this.rd = read }
}

