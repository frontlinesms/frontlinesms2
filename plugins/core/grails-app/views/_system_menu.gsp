<%@ page contentType="text/html;charset=UTF-8" %>
<div id="logo"></div>
<ul id="system-nav">
	<li class="${params.controller=='settings'?'selected':''}">
		<g:link controller="settings">
			<g:message code="common.settings" />
		</g:link>
	</li>
	<li class="nav ${params.controller=='help' ? 'selected' : ''}">
		<g:remoteLink controller="help" onSuccess="launchMediumWizard(i18n('popup.help.title'), data, i18n('popup.done'), '95%', 800)">
			<g:message code="commont.help" />
		</g:remoteLink>
	</li>
</ul>
<r:script>
	function initializePopup() {}
</r:script>
