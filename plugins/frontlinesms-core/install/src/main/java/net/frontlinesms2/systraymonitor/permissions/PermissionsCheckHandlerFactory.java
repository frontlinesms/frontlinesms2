package net.frontlinesms2.systraymonitor.permissions;

public class PermissionsCheckHandlerFactory {
	public PermissionsCheckHandler create() {
		String osName = System.getProperty("os.name");
		if(osName == null) osName = "";
		osName = osName.toLowerCase();

		if(!osName.contains("windows") && !osName.contains("mac")) {
			return new UnixPermissionsCheckHandler();
		}
		return new NoPermissionsCheckHandler();
	}
}

class NoPermissionsCheckHandler implements PermissionsCheckHandler {
	public void check() {}
}

