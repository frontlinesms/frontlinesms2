<%@ page contentType="text/html;charset=UTF-8" %>
<div id="body-menu">
	<ul>
		<li class="${params.action=='general' ? 'selected' : ''}">
			<g:link url="${[controller:'settings', action:'general']}"><g:message code="settings.general"/></g:link>
		</li>
		<li class="${params.controller=='connection' ? 'selected' : ''}">
			<g:link url="${[controller:'connection', action:'list']}"><g:message code="settings.connections"/></g:link>
		</li>
		<li class="${params.action=='logs' ? 'selected' : ''}">
			<g:link url="${[controller:'settings', action:'logs']}"><g:message code="settings.logs"/></g:link>
		</li>
	</ul>
</div>

