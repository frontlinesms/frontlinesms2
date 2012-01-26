package net.frontlinesms2.systraymonitor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JOptionPane;

import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;

import static net.frontlinesms2.systraymonitor.Utils.*;

public class TrayThingy implements Listener {
	private final Monitor m;
	private TrayIcon t;
	private PopupMenu popup;
	private MenuItem open;
	private MenuItem start;
	private MenuItem stop;

//> INITIALISATION
	public TrayThingy(Monitor m) {
		this.m = m;
		m.addListener(this);
	}

	private void init() {
		popup = createPopupMenu();
		t = new TrayIcon(getIcon("stopped"), "FrontlineSMS2", popup);
		t.setImageAutoSize(true);
		t.addActionListener(createActionListener());
		d(m.getServer());
	}

//> LifeCycle.Listener METHODS
	private void updateStatus(String name) {
		t.setToolTip("FrontlineSMS2 :: " + name + "...");
		t.setImage(getIcon(name));
	}

	private Image getIcon(String name) {
		return Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tray/" + name + ".png"));
	}

	public void lifeCycleFailure(LifeCycle event, Throwable cause) {
		d(event);
		cause.printStackTrace();
	}
	public void lifeCycleStarted(LifeCycle event) { d(event); }
	public void lifeCycleStarting(LifeCycle event) { d(event); }
	public void lifeCycleStopped(LifeCycle event) { d(event); }
	public void lifeCycleStopping(LifeCycle event) { d(event); }
	private void d(LifeCycle e) {
		o("Lifecyle event: " + e);
		o("__isFailed(): " + e.isFailed());
		o("__isRunning(): " + e.isRunning());
		o("__isStarted(): " + e.isStarted());
		o("__isStarting(): " + e.isStarting());
		o("__isStopped(): " + e.isStopped());
		o("__isStopping(): " + e.isStopping());

		if(e.isStarting()) updateStatus("starting");
		else if(e.isStopping()) updateStatus("stopping");
		else if(e.isStopped()) updateStatus("stopped");
		else if(e.isRunning()) updateStatus("running");
		else o("Not sure what to set the icon to in this state.");

		open.setEnabled(e.isRunning() && !e.isStarting() && !e.isStopping());
		start.setEnabled(!e.isRunning());
		stop.setEnabled(e.isRunning() && !e.isStarting() && !e.isStopping());
	}

	private PopupMenu createPopupMenu() {
		PopupMenu popup = new PopupMenu();
			
		popup.add(new ClickMenuItem("About FrontlineSMS2 alpha XXX") {
			void click() {
				JOptionPane.showMessageDialog(null, "FrontlineSMS alpha - thanks for helping us test :-)");
			}});

		popup.add(open = new ClickMenuItem("Open FrontlineSMS") {
			void click() { openWebBrowser(m.getUrl()); }});
	
		popup.add(start = new ClickMenuItem("Start service") {
			void click() throws Exception { m.start(); }});

		popup.add(stop = new ClickMenuItem("Stop service") {
			void click() throws Exception { m.stop(); }});

		popup.add(new ClickMenuItem("Exit JVM") {
			void click() { System.exit(0); }});

		return popup;
	}

	private ActionListener createActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openWebBrowser(m.getUrl());
			}
		};
	}

	public TrayIcon getTrayIcon() {
		if(t == null) init();
		return t;
	}
}

abstract class ClickMenuItem extends MenuItem {
	public ClickMenuItem(String text) {
		super(text);
		this.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					click();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}
	abstract void click() throws Exception;
}

