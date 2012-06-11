package frontlinesms2

class AppSettingsService {
	private static final File PROPERTIES_FILE = new File(new File(System.properties['user.home'], '.frontlinesms2'), 'app-settings.properties')
	private def settings = [:]

	synchronized def init() {
		load()
	}

	synchronized def set(key, value) {
		settings[key] = value
		persist()
	}

	synchronized def get(key) {
		settings[key]
	}

	private synchronized def load() {
		try {
			def p = new Properties()
			PROPERTIES_FILE.withInputStream { stream -> p.load(stream) }

			def m = [:]
			p.each { k, v -> m[k] = v }

			settings = m
		} catch(Exception ex) {
			ex.printStackTrace()
			// probably not the end of the world
		}
	}

	private synchronized def persist() {
		try {
			def p = new Properties()
			settings.each { k, v -> p.setProperty(k, v) }

			PROPERTIES_FILE.withOutputStream { stream -> p.store(stream, 'FrontlineSMS Settings') }
		} catch(Exception ex) {
			ex.printStackTrace()
			// probably not the end of the world
		}
	}
}
