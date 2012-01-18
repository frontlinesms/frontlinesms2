package net.frontlinesms2.systraymonitor;

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
}

