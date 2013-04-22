<%@ page import="frontlinesms2.ConnectionStatus" %>
<div class="controls">
	<g:if test="${c.status in [ConnectionStatus.FAILED, ConnectionStatus.DISABLED, ConnectionStatus.NOT_CONNECTED]}">
		<g:link controller="connection" action="enable" class="btn route" id="${c.id}">
			<g:if test="${c.status  == ConnectionStatus.FAILED}">
				<g:message code="connection.route.retryconnection"/>
			</g:if>
			<g:else>
				<g:message code="connection.route.enable"/>
			</g:else>
		</g:link>
	</g:if>
	<g:if test="${c.status in [ConnectionStatus.FAILED, ConnectionStatus.DISABLED, ConnectionStatus.NOT_CONNECTED]}">
		<g:remoteLink controller="connection" action="wizard" class="btn route" id="${c.id}" onSuccess="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
			<g:message code="connection.edit"/>
		</g:remoteLink>
	</g:if>
	<g:if test="${c.status in [ConnectionStatus.FAILED,ConnectionStatus.DISABLED, ConnectionStatus.NOT_CONNECTED]}">
		<g:link controller="connection" action="delete" class="btn route" id="${c.id}">
			<g:message code="connection.delete"/>
		</g:link>
	</g:if>
	<g:if test="${c.status in [ConnectionStatus.CONNECTED]}">
		<g:remoteLink controller="connection" action="createTest" class="btn test" id="${c.id}"
				onSuccess="launchSmallPopup(i18n('smallpopup.test.message.title'), data, i18n('action.send'))">
			<g:message code="connection.send.test.message"/>
		</g:remoteLink>
	</g:if>
	<g:if test="${c.status in [ConnectionStatus.FAILED, ConnectionStatus.CONNECTED]}">
		<g:link controller="connection" action="disable" class="btn" id="${c.id}">
			<g:message code="connection.route.destroy"/>
		</g:link>
	</g:if>
</div>

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

