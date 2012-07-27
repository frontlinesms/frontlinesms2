package net.frontlinesms2.systraymonitor;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

import static net.frontlinesms2.systraymonitor.Utils.*;
import static net.frontlinesms2.systraymonitor.CommandlineUtils.*;

public class Main {
	private static final String PROP_SERVER_PORT = "server.port";
	private static final String PROP_TRAY_DISABLED = "tray.disabled";

	private Monitor m;
	private TrayThingy t;
	private boolean trayIconDisabled;

//> MAIN AND COMMANDLINE 
	public static void main(String... args) throws Exception {
		o("main() called with args: " + Arrays.toString(args));
		c("org/apache/juli/logging/LogFactory");
		c("org.apache.jasper.servlet.JspServlet");

		o("Reading properties file...");
		FProperties properties = new FProperties("launcher.properties");
		o("Properties file read.");

		// Set defailt properties
		properties.setDefault(PROP_SERVER_PORT, 8129);
		properties.setDefault(PROP_TRAY_DISABLED, false);

		// Override properties with commandline settings
		if(isFlagSet(args, "no-tray")) properties.set(PROP_TRAY_DISABLED, true);
		if(isValSet(args, "server-port")) properties.set(PROP_SERVER_PORT, getVal(args, "server-port"));

		Main m = new Main();
		m.init(properties);
	}

//> ACCESSORS
	private void setTrayIconDisabled(boolean disabled) {
		o("Setting tray disabled to: " + disabled);
		this.trayIconDisabled = disabled;
	}

//> INIT
	private void init(FProperties properties) throws Exception {
		setTrayIconDisabled(properties.getBoolean(PROP_TRAY_DISABLED));

		int port = properties.getInt(PROP_SERVER_PORT);
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

