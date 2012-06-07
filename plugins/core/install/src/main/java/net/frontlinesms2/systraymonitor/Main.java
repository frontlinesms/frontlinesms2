package net.frontlinesms2.systraymonitor;

import java.awt.*;
import java.awt.event.*;

import static net.frontlinesms2.systraymonitor.Utils.*;

public class Main {
	private Monitor m;
	private TrayThingy t;

	public static void main(String... args) throws Exception {
		c("org/apache/juli/logging/LogFactory");
		c("org.apache.jasper.servlet.JspServlet");

		o("Starting FrontlineSMS2 system tray monitor...");

		if(SystemTray.isSupported()) {
			new Main().init();
		} else {
			e("SystemTray not supported on this system.");
		}
	}

	private void init() throws Exception {
		o("Reading properties file...");
		FProperties properties = new FProperties("launcher.properties");
		o("Properties file read.");
		int port = properties.getInt("server.port", 8080);
		o("Read server port: " + port);

		o("Creating monitor...");
		m = new Monitor();
		o("monitor created.");

		o("Initialising monitor...");
		m.setPort(port);
		m.init();
		o("Monitor initialised.");

		while(false && !m.isStarted()) {
			o("Waiting for server to start...");
			Thread.sleep(1000);
		}

		o("Adding tray icon...");
		t = new TrayThingy(m);
		SystemTray.getSystemTray().add(t.getTrayIcon());
		o("Tray icon added.");

		// TODO may want to only start when told to
		o("Starting tomcat...");
		m.start();
		o("start command issued.");
	}
}

