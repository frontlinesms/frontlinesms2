package frontlinesms2

class ResourceUtils {
	/* N.B. Unfortunately this code is currently triplcated throughout application.  Please take care when editing.  FIXME move to external lib. */
	static String getResourcePath() {
		def path = System.getProperty("frontlinesms.resource.path");
		if(!path) return System.getProperty("user.home") + File.separatorChar + "/.frontlinesms2-default"
		else if(path[0] == '~') return System.getProperty("user.home") + path.substring(1);
		else return path;
	}

	/* N.B. Unfortunately this code is currently triplcated throughout application.  Please take care when editing.  FIXME move to external lib. */
	static File getResourceDirectory() {
		new File(getResourcePath())
	}
}

