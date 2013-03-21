package frontlinesms2

class AppInfoService {
	def grailsApplication
	def providers = [:]

	synchronized void registerProvider(String key, Closure provider) {
		if(providers.containsKey(key)) {
			throw new RuntimeException("Provider already registered for key: $key")
		}
		providers[key] = provider
	}

	def provide(AppInfoController controller, String key, data) {
		def provider
		synchronized(this) {
			provider = providers[key]
		}
		return provider?.call(grailsApplication, controller, data)
	}
}

