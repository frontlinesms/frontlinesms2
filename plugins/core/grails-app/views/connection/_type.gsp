<%@ page import="frontlinesms2.*" %>
<div id="tabs-1">
	<ul id="type-list" class="${fconnectionInstance ? 'hide': ''}">
		<g:each in="${[SmslibFconnection, ClickatellFconnection, EmailFconnection]}">
			<li>
				<g:set var="type" value="${it.simpleName.toLowerCase() - 'fconnection'}"/>
				<g:radio class="${type}" name="connectionType"
						value="${type}" onclick="setConnectionType('${type}')"
						checked="${type=='smslib'}"/>
				<g:message code="${it.simpleName.toLowerCase()}.label"/>
			</li>
		</g:each>
	</ul>
</div>
