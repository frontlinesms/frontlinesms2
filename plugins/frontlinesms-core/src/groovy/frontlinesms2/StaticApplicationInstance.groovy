package frontlinesms2

class StaticApplicationInstance {
	private static final String APPINSTANCE_ID = 'frontlinesms.appInstanceId'
	static synchronized String getUniqueId() {
		def id = System.properties[APPINSTANCE_ID]
		if(!id) {
			id = "${new Random().nextLong()}"
			System.properties[APPINSTANCE_ID] = id
		}
		return id
	}
}

