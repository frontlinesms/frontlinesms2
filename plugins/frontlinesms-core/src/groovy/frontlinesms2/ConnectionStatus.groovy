package frontlinesms2

enum ConnectionStatus {
	NOT_CONNECTED,
	CONNECTING,
	CONNECTED,
	DISABLED,
	FAILED

	String getI18n() {
		getClass().simpleName.toLowerCase() + "." + name().toLowerCase()
	}	
}

