<%@ page import="frontlinesms2.*" %>
<div id="tabs-3" class='confirm'>
	<h3><g:message code="connection.confirm.header"/></h3>
	
	<g:each in="${Fconnection.implementations}">
		<fsms:confirmTable instanceClass="${it}"/>
	</g:each>
</div>
