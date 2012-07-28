package net.frontlinesms2.systraymonitor;

import java.io.File;

public class Utils {
	public static void c(String className) {
		className = className.replace('/', '.');
		Class c = null;
		try {
			c = Class.forName(className);
		} catch(ClassNotFoundException ex) {}
		o("Class " + className + " available? " + (c!=null));
	}

	public static void o(String s) {
		System.out.println(s);
	}

	public static void e(String s) {
		System.err.println(s);
	}

	public static void openWebBrowser(String url) {
		BareBonesBrowserLaunch.openURL(url);
	}

	/* N.B. Unfortunately this code is currently triplcated throughout application.  Please take care when editing.  FIXME move to external lib. */
	public static String getResourcePath() {
		String path = System.getProperty("frontlinesms.resource.path");
		if(path == null || path.length() == 0) return System.getProperty("user.home") + File.separatorChar + "/.frontlinesms2-default";
		else if(path.charAt(0) == '~') return System.getProperty("user.home") + path.substring(1);
		else return path;
	}

	/* N.B. Unfortunately this code is currently triplcated throughout application.  Please take care when editing.  FIXME move to external lib. */
	public static File getResourceDirectory() {
		return new File(getResourcePath());
	}
}

