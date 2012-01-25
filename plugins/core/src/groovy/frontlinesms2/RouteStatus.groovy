package frontlinesms2

enum RouteStatus {
	NOT_CONNECTED,
	CONNECTED
	
	String toString() {
		super.toString().toLowerCase().capitalize().replaceAll("_"," ")
	}
}

