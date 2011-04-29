package frontlinesms2

enum EmailProtocol {
	IMAP, IMAPS, POP3, POP3S;
	
	String toString() { return name().toLowerCase() }
}
