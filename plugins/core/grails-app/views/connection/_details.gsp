<%@ page import="frontlinesms2.*" %>
<div id="tabs-2">
	<g:each in="${Fconnection.implementations}">
		<div id="${it.shortName}-form">
			<h2><g:message code="${it.simpleName.toLowerCase()}.label"/></h2>
			<fsms:inputs instance="${fconnectionInstance}" instanceClass="${it}"/>
		</div>
	</g:each>
</div>
