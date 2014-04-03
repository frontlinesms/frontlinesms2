<%@ page import="frontlinesms2.ConnectionStatus" %>
<html>
<head>
	<meta name="layout" content="connection"/>
	<title><g:message code="connection.header"/> ${connectionInstance?.name}</title>
</head>
<body>
<div id="body-content-head">
	<div class="content">
		<h1><g:message code="connection.header"/></h1>
		<ul class="buttons">
			<li>
				<fsms:popup class="btn" name="addConnection" controller='connection' action="wizard" popupCall="mediumPopup.launchMediumWizard(i18n('connection.add'), data, i18n('wizard.create'), 675, 500, false)">
					<g:message code="connection.add"/>
				</fsms:popup>
			</li>
		</ul>
	</div>
</div>
<div id="body-content" class="connections">
	<g:if test="${fconnectionInstanceTotal==0}">
		<p class="no-content"><g:message code="connection.list.none"/></p>
	</g:if>
	<g:else>
		<table class="connection-list">
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<fsms:render template="connection" model="[c:c]"/>
			</g:each>
		</table>
	</g:else>
	<fsms:render template="routing"/>
</div>

<script id="fconnection-controls-FAILED" type="text/x-sanchez-template">
	<g:link controller="connection" action="enable" class="btn route" id="{{connectionId}}"><g:message code="connection.route.retryconnection"/></g:link>
	<g:link controller="connection" action="disable" class="btn" id="{{connectionId}}">
		<g:message code="connection.route.disable"/>
	</g:link>
	<fsms:popup controller="connection" action="wizard" class="btn route" id="{{connectionId}}" popupCall="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
		<g:message code="connection.edit"/>
	</fsms:popup>
</script>

<script id="fconnection-controls-NOT_CONNECTED" type="text/x-sanchez-template">
	<g:link controller="connection" action="enable" class="btn route" id="{{connectionId}}"><g:message code="connection.route.retryconnection"/></g:link>
	<g:link controller="connection" action="disable" class="btn" id="{{connectionId}}">
		<g:message code="connection.route.disable"/>
	</g:link>
	<fsms:popup controller="connection" action="wizard" class="btn route" id="{{connectionId}}" popupCall="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
		<g:message code="connection.edit"/>
	</fsms:popup>
</script>

<script id="fconnection-controls-DISABLED" type="text/x-sanchez-template">
	<g:link controller="connection" action="enable" class="btn route" id="{{connectionId}}"><g:message code="connection.route.enable"/></g:link>
	<fsms:popup controller="connection" action="wizard" class="btn route" id="{{connectionId}}" popupCall="mediumPopup.launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)">
		<g:message code="connection.edit"/>
	</fsms:popup>
	<g:link controller="connection" action="delete" class="btn route" id="{{connectionId}}">
		<g:message code="connection.delete"/>
	</g:link>
</script>

<script id="fconnection-controls-CONNECTED" type="text/x-sanchez-template">
	<fsms:popup controller="connection" action="createTest" class="btn test" id="{{connectionId}}" popupCall="launchSmallPopup(i18n('smallpopup.test.message.title'), data, i18n('action.send'))">
		<g:message code="connection.send.test.message"/>
	</fsms:popup>
	<g:link controller="connection" action="disable" class="btn" id="{{connectionId}}">
		<g:message code="connection.route.disable"/>
	</g:link>
</script>

<script id="fconnection-controls-CONNECTING" type="text/x-sanchez-template">
	<g:link controller="connection" action="delete" class="btn route" id="{{connectionId}}">
		<g:message code="connection.delete"/>
	</g:link>
</script>


<g:javascript>
$(function() {
	<g:each in="${connectionInstanceList}" status="i" var="c">
		<g:if test="${c.isUserMutable()}">
			fconnection_list.update("${c.status}", ${c.id});
		</g:if>
	</g:each>
	app_info.listen("fconnection_statuses", function(data) {
		var i;
		data = data.fconnection_statuses;
		if(!data) { return; }
		for(i=data.length-1; i>=0; --i) {
			if(data[i].userMutable) {
				fconnection_list.update(data[i].status, data[i].id);
			}
		}
	});

	app_info.listen("frontlinesync_config_synced_status", function(data){
		var i, data = data.frontlinesync_config_synced_status;
		for(i=data.length-1; i>=0; --i) {
			frontlinesync.updateConfigSynced(data[i]);
		}
	});
	preloadImage("${r.resource(dir:'images', file:'message/gray-ajax-spinner.gif')}");
	<g:if test="${newConnectionIds}">
		fconnection_list.pulseNewConnections("${newConnectionIds}");
	</g:if>
});
</g:javascript>
</body>
</html>

