<div class="controls"></div>

<script id="fconnection-controls-FAILED" type="text/x-sanchez-template">
	<g:link controller="connection" action="createRoute" class="btn route" id="{{connectionId}}"><g:message code="connection.route.create"/></g:link>
	<g:remoteLink controller="connection" action="wizard" class="btn route" id="{{connectionId}}"
			onSuccess="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
		<g:message code="connection.edit"/>
	</g:remoteLink>
	<g:link controller="connection" action="destroyRoute" class="btn" id="{{connectionId}}">
		<g:message code="connection.route.destroy"/>
	</g:link>
	<g:link controller="connection" action="delete" class="btn route" id="{{connectionId}}">
		<g:message code="connection.delete"/>
	</g:link>
</script>

<script id="fconnection-controls-DISABLED" type="text/x-sanchez-template">
	<g:link controller="connection" action="createRoute" class="btn route" id="{{connectionId}}"><g:message code="connection.route.create"/></g:link>
	<g:remoteLink controller="connection" action="wizard" class="btn route" id="{{connectionId}}"
			onSuccess="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
		<g:message code="connection.edit"/>
	</g:remoteLink>
	<g:link controller="connection" action="destroyRoute" class="btn" id="{{connectionId}}">
		<g:message code="connection.route.destroy"/>
	</g:link>
	<g:link controller="connection" action="delete" class="btn route" id="{{connectionId}}">
		<g:message code="connection.delete"/>
	</g:link>
</script>

<script id="fconnection-controls-CONNECTED" type="text/x-sanchez-template">
	<g:remoteLink controller="connection" action="createTest" class="btn test" id="{{connectionId}}"
			onSuccess="launchSmallPopup(i18n('smallpopup.test.message.title'), data, i18n('action.send'))">
		<g:message code="connection.send.test.message"/>
	</g:remoteLink>
	<g:link controller="connection" action="destroyRoute" class="btn" id="{{connectionId}}">
		<g:message code="connection.route.destroy"/>
	</g:link>
</script>

<script id="fconnection-controls-CONNECTING" type="text/x-sanchez-template">
</script>

<g:javascript>
$(function() {
	fconnection_show.update("${c.status}", ${c.id});
});
</g:javascript>

