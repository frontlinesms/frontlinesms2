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
        echo("group now? [y/N]");
        boolean addToGroup = false;
        prompt:while(true) {
            switch(charPrompt('n')) {
                case 'y': case 'Y':
                    addToGroup = true;
                case 'n': case 'N':
                    break prompt;
                default: echo("Please enter either 'Y' or 'N'.");
            }
        }
        if(addToGroup) {
            echo("Adding to group...");
            u.addToGroup(MODEM_GROUP);
        } else echo("Skipping add to group.");
    }
}

