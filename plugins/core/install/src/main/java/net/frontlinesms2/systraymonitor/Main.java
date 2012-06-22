package net.frontlinesms2.systraymonitor;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

import static net.frontlinesms2.systraymonitor.Utils.*;

public class Main {
	private Monitor m;
	private TrayThingy t;
	private boolean trayIconDisabled;

//> MAIN AND COMMANDLINE 
	public static void main(String... args) throws Exception {
		o("main() called with args: " + Arrays.toString(args));
		c("org/apache/juli/logging/LogFactory");
		c("org.apache.jasper.servlet.JspServlet");

		Main m = new Main();
		m.setTrayIconDisabled(isFlagSet(args, "--no-tray"));
		m.init();
	}

	private static boolean isFlagSet(String[] args, String flag) {
		for(String arg : args) {
			if(arg.equals(flag)) {
				return true;
			}
		}
		return false;
	}

//> ACCESSORS
	private void setTrayIconDisabled(boolean disabled) {
		this.trayIconDisabled = disabled;
	}

//> INIT
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

		if(SystemTray.isSupported()) {
			if(trayIconDisabled) {
				o("Tray icon disabled.");
			} else {
				o("Adding tray icon...");
				t = new TrayThingy(m);
				SystemTray.getSystemTray().add(t.getTrayIcon());
				o("Tray icon added.");
			}
		} else {
			e("SystemTray not supported on this system.");
		}

		// TODO may want to only start when told to
		o("Starting server on port " + port + "...");
		m.start();
		o("start command issued.");
	}
}

