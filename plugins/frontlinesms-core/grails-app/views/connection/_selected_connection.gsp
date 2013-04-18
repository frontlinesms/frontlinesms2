<%-- TODO when sanchez template rndering taglib is included, refactor here to use that instead of explicit sanchez templates --%>
<div class="controls"></div>

<script id="fconnection-controls-FAILED" type="text/x-sanchez-template">
	<g:link controller="connection" action="enable" class="btn route" id="{{connectionId}}"><g:message code="connection.route.retryconnection"/></g:link>
	<g:link controller="connection" action="disable" class="btn" id="{{connectionId}}">
		<g:message code="connection.route.destroy"/>
	</g:link>
	<fsms:popup controller="connection" action="wizard" class="btn route" id="{{connectionId}}" method="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
		<g:message code="connection.edit"/>
	</fsms:popup>
	<g:link controller="connection" action="delete" class="btn route" id="{{connectionId}}">
		<g:message code="connection.delete"/>
	</g:link>
</script>

<script id="fconnection-controls-DISABLED" type="text/x-sanchez-template">
	<g:link controller="connection" action="enable" class="btn route" id="{{connectionId}}"><g:message code="connection.route.enable"/></g:link>
	<fsms:popup controller="connection" action="wizard" class="btn route" id="{{connectionId}}" method="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
		<g:message code="connection.edit"/>
	</fsms:popup>
	<g:link controller="connection" action="delete" class="btn route" id="{{connectionId}}">
		<g:message code="connection.delete"/>
	</g:link>
</script>

<script id="fconnection-controls-NOT_CONNECTED" type="text/x-sanchez-template">
	<g:link controller="connection" action="enable" class="btn route" id="{{connectionId}}"><g:message code="connection.route.enable"/></g:link>
	<fsms:popup controller="connection" action="wizard" class="btn route" id="{{connectionId}}" method="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
		<g:message code="connection.edit"/>
	</fsms:popup>
	<g:link controller="connection" action="delete" class="btn route" id="{{connectionId}}">
		<g:message code="connection.delete"/>
	</g:link>
</script>

<script id="fconnection-controls-CONNECTED" type="text/x-sanchez-template">
	<fsms:popup controller="connection" action="createTest" class="btn test" id="{{connectionId}}" method="launchSmallPopup(i18n('smallpopup.test.message.title'), data, i18n('action.send'))">
		<g:message code="connection.send.test.message"/>
	</fsms:popup>
	<g:link controller="connection" action="disable" class="btn" id="{{connectionId}}">
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

