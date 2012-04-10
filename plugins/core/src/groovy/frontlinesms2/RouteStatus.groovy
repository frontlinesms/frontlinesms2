package frontlinesms2

enum RouteStatus {
	NOT_CONNECTED,
	CONNECTING,
	CONNECTED

	String getI18n() {
		getClass().simpleName.toLowerCase() + "." + name().toLowerCase()
	}	
}

