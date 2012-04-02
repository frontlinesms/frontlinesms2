<%@ page import="frontlinesms2.*" %>
<div id="tabs-3" class='confirm'>
	<h3><g:message code="connection.confirm.header" /></h3>
	<fsms:confirmTable instanceClass="${SmslibFconnection}"
			fields="name, type, port, baud, pin, smsc, imsi, serial"/>
	<fsms:confirmTable instanceClass="${EmailFconnection}"
			fields="name, type, serverName, serverPort, username, password"/>
	<fsms:confirmTable instanceClass="${ClickatellFconnection}"
			fields="name, type, apiId, username, password"/>
</div>