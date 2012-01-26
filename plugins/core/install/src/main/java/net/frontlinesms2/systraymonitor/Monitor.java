package net.frontlinesms2.systraymonitor;

import java.awt.*;
import java.awt.event.*;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Monitor {
	private Server server;
	private int port;

	public Monitor() {}

	public void setPort(int port) {
		this.port = port;
	}

	public void init() throws Exception {
		server = new Server(port);

		WebAppContext app = new WebAppContext();
		app.setContextPath("/");
		app.setWar(new File("webapp").getAbsolutePath());
		server.setHandler(app);
	}

	public void addListener(org.eclipse.jetty.util.component.LifeCycle.Listener listener) {
		server.addLifeCycleListener(listener);
	}

	public void start() throws Exception {
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public Server getServer() {
		return server;
	}

	public boolean isStarted() {
		return server.isStarted();
	}

	public boolean isRunning() {
		return server.isRunning();
	}

	public String getUrl() {
		return "http://localhost:8080";
	}
}

