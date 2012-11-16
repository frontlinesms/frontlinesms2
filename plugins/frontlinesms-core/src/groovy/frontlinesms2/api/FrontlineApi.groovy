package frontlinesms2.api

/**
 * Interface to be applied to domain classes who exhibit a public API.
 * Classes implementing this interface should also implement a static String
 * `apiUrl` which indicates the URL to be used when accessing these objects
 * externally.
 */
interface FrontlineApi {
	String getSecret();
	def apiProcess(controller);
	boolean isApiEnabled();
	Long getId();
	String getFullApiUrl();
}

