<%@ page contentType="text/html;charset=UTF-8" %>
<ul id="system-menu">
	<li class="${params.controller=='settings'?'selected':''}">
		<g:link controller="settings">
			<g:message code="common.settings"/>
		</g:link>
	</li>
	<li class="nav ${params.controller=='help' ? 'selected' : ''}">
		<g:remoteLink controller="help" onSuccess="launchHelpWizard(data);">
			<g:message code="commont.help"/>
		</g:remoteLink>
	</li>
</ul>

