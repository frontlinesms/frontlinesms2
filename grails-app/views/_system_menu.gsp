<%@ page contentType="text/html;charset=UTF-8" %>
<ul id="system-menu">
	<li>
		<g:remoteLink controller="settings" onSuccess="launchMediumWizard('Settings & Plugins', data, 'Done');">
			Settings & Plugins
		</g:remoteLink>
	</li>
</ul>