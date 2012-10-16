package net.frontlinesms2.systraymonitor.permissions;

import net.frontlinesms2.systraymonitor.system.CommandlineUserInteraction;
import net.frontlinesms2.systraymonitor.system.UnixUser;

public class UnixPermissionsCheckHandler extends CommandlineUserInteraction implements PermissionsCheckHandler {
	private static final String MODEM_GROUP = "dialout";
	private UnixUser u = new UnixUser();
	public void check() {
		u.initGroups();
		if(u.isMemberOf(MODEM_GROUP)) return;
		u.initUsername();
		echo("You must be a member of group '" + MODEM_GROUP + "' to access serial devices");
		echo("(e.g. modems and phones) on this operating system.");
		echo("Would you like to add your user (" + u.getUsername() + ") to this");
		if(yesNoPrompt("group now?", false)) {
			echo("Adding to group...");
			u.addToGroup(MODEM_GROUP);
			echo("You must now restart your computer, or log out and log in again");
			echo("for the permission changes to take effect.");
			if(yesNoPrompt("Quit FrontlineSMS now?", true)) System.exit(0);
		} else echo("Skipping add to group.");
	}
}

