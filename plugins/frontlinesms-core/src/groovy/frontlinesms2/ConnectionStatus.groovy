package frontlinesms2

enum ConnectionStatus {
	NOT_CONNECTED,
	CONNECTING,
	CONNECTED,
	DISABLED

	String getI18n() {
		getClass().simpleName.toLowerCase() + "." + name().toLowerCase()
	}	
}

