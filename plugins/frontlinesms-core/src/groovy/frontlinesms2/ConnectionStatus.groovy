package frontlinesms2

enum ConnectionStatus {
	CONNECTING,
	CONNECTED,
	DISABLED,
	FAILED

	String getI18n() {
		getClass().simpleName.toLowerCase() + "." + name().toLowerCase()
	}	
}

