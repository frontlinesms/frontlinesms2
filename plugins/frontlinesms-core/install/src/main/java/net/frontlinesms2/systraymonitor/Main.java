package net.frontlinesms2.systraymonitor;

import java.awt.*;
import java.awt.event.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import net.frontlinesms2.systraymonitor.permissions.PermissionsCheckHandlerFactory;

import static net.frontlinesms2.systraymonitor.Utils.*;
import static net.frontlinesms2.systraymonitor.CommandlineUtils.*;

public class Main {
	private static final String PROP_SERVER_PORT = "server.port";
	private static final String PROP_TRAY_DISABLED = "tray.disabled";
	private static final String PROP_RESOURCE_PATH = "resource.path";
	private static final String PROP_PERMISSIONS_CHECK = "os.permissions.check";
	private static final String PROP_SERIAL_PORTS = "serial.ports.rxtx.enable";
	private static final String PROP_SERIAL_DETECTION_DISABLED = "serial.detect.disable";
	private static final String PROP_RESTORE_DB_BACKUP = "restore.db.backup";

	private Monitor m;
	private TrayThingy t;
	private boolean trayIconDisabled;

//> MAIN AND COMMANDLINE 
	public static void main(String... args) throws Exception {
		o("main() called with args: " + Arrays.toString(args));
		c("org/apache/juli/logging/LogFactory");
		c("org.apache.jasper.servlet.JspServlet");

		o("Default charset: " + Charset.defaultCharset());

		o("Reading properties file...");
		FProperties properties = new FProperties("launcher.properties");
		o("Properties file read.");

		// Set defailt properties
		properties.setDefault(PROP_SERVER_PORT, 8129);
		properties.setDefault(PROP_TRAY_DISABLED, false);
		properties.setDefault(PROP_RESOURCE_PATH, "~/.frontlinesms2-default");
		properties.setDefault(PROP_PERMISSIONS_CHECK, true);
		properties.setDefault(PROP_SERIAL_DETECTION_DISABLED, false);
		properties.setDefault(PROP_RESTORE_DB_BACKUP, false);

		// Override properties with commandline settings
		if(isFlagSet(args, "no-tray")) properties.set(PROP_TRAY_DISABLED, true);
		if(isFlagSet(args, "no-permission-check")) properties.set(PROP_PERMISSIONS_CHECK, false);
		if(isFlagSet(args, "no-serial-detection")) properties.set(PROP_SERIAL_DETECTION_DISABLED, true);
		if(isFlagSet(args, "restore-database-backup")) properties.set(PROP_RESTORE_DB_BACKUP, true);
		mapArgsToProperties(args, properties,
				"server-port", PROP_SERVER_PORT,
				"resource-path", PROP_RESOURCE_PATH);

		setResourcePathSystemProperty(properties);

		Main m = new Main();
		m.init(properties);
	}

	private static void mapArgsToProperties(String[] args, FProperties properties, String... mappings) {
		for(int i=0; i<mappings.length; i+=2) {
			String commandlineKey = mappings[i];
			String propertyFileKey = mappings[i+1];
			if(isValSet(args, commandlineKey)) {
				properties.set(propertyFileKey, getVal(args, commandlineKey));
			}
		}
	}

	private static void setResourcePathSystemProperty(FProperties properties) {
		System.setProperty("frontlinesms.resource.path", properties.getString(PROP_RESOURCE_PATH));
	}

//> ACCESSORS
	private void setTrayIconDisabled(boolean disabled) {
		o("Setting tray disabled to: " + disabled);
		this.trayIconDisabled = disabled;
	}

//> INIT
	private void init(FProperties properties) throws Exception {
		setTrayIconDisabled(properties.getBoolean(PROP_TRAY_DISABLED));

		if(properties.getBoolean(PROP_PERMISSIONS_CHECK)) {
			new PermissionsCheckHandlerFactory().create().check();
		}

		String serialPorts = properties.getString(PROP_SERIAL_PORTS);
		if(serialPorts != null) {
			System.setProperty("gnu.io.rxtx.SerialPorts", serialPorts);
		}

		if(properties.getBoolean(PROP_SERIAL_DETECTION_DISABLED)) {
			System.setProperty("serial.detect.disable", "true");
		}

		o("Checking if DB Restore was requested..");
		if(properties.getBoolean(PROP_RESTORE_DB_BACKUP)) {
			o("Running Database restore");
			(new DatabaseBackupRestorer()).restore(properties.getString(PROP_RESOURCE_PATH));
		}

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

