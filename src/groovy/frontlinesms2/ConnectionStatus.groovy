package frontlinesms2

enum ConnectionStatus {
	NOT_CONNECTED,
	CONNECTED,
	ERROR

	String toString() {
		super.toString().toLowerCase().capitalize().replaceAll("_"," ")
	}
}