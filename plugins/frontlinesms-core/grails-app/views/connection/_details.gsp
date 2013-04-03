<%@ page import="frontlinesms2.*" %>
<g:if test="${fconnectionInstance}">
	<div id="${fconnectionInstance.shortName}-form" class="fconnection-details">
		<h2><g:message code="${fconnectionInstance.shortName}.label"/></h2>
		<fsms:inputs instance="${fconnectionInstance}" instanceClass="${fconnectionInstance.class}" list="true"/>
	</div>
</g:if>
<g:else>
	<g:each in="${Fconnection.implementations}">
		<div id="${it.shortName}-form" class="fconnection-details">
			<h2><g:message code="${it.shortName}.label"/></h2>
			<fsms:inputs instance="${fconnectionInstance}" instanceClass="${it}" list="true"/>
		</div>
	</g:each>
</g:else>
