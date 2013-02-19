package frontlinesms2

enum ConnectionStatus {
	CONNECTING,
	CONNECTED,
	DISABLED,
	FAILED,
	NOT_CONNECTED

	String getI18n() {
		getClass().simpleName.toLowerCase() + "." + name().toLowerCase()
	}	
}

