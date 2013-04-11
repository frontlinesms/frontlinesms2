<%@ page contentType="text/html;charset=UTF-8" %>
<fsms:menu>
	<fsms:menuitem selected="${params.action=='general'}" controller="settings" action="general" code="settings.general" />
	<fsms:menuitem selected="${params.action=='porting'}" controller="settings" action="porting" code="settings.porting" />
	<fsms:menuitem selected="${params.controller=='connection'}" controller="connection" action="list" code="settings.connections" />
	<fsms:menuitem selected="${params.action=='logs'}" controller="settings" action="logs" code="settings.logs" />
</fsms:menu>
