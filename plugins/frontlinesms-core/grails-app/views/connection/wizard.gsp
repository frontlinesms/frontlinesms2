<%@ page contentType="text/html;charset=UTF-8" import="frontlinesms2.Fconnection" %>
<meta name="layout" content="popup"/>
<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="connection.validation.prompt" /></div>
	<ol>
		<g:if test="${!fconnectionInstance}">
			<li><a href="#tabs-1"><g:message code="connection.type" /></a></li>
		</g:if>
		<li><a href="#tabs-2"><g:message code="connection.details" /></a></li>
		<li><a href="#tabs-3"><g:message code="connection.confirm" /></a></li>
	</ol>
	<g:formRemote name="connectionForm" url="[controller:'connection', action:action, id:fconnectionInstance?.id, params:[format:'json']]" method="post" onLoading="showThinking()" onSuccess="hideThinking(); fconnection.handleSaveResponse(data)">
		<fsms:wizardTabs templates="type, details, confirm"/>
	</g:formRemote>
</div>

<r:script>
fconnection.getType = function() {
	<g:if test="${fconnectionInstance}">return "${fconnectionInstance?.shortName}";</g:if>
	<g:else>return $("input[name=connectionType]:checked").val();</g:else>
};

fconnection.setType = function(connectionType) {
	if(!$("input[name=connectionType]:checked")) {
		$("input[name=connectionType]").each(function() {
			var e = $(this);
			e.attr("checked", e.val() == connectionType);
		});
	}
	<g:each in="${Fconnection.implementations*.shortName}">
		$("#${it}-form").hide();
	</g:each>
	$("#" + connectionType + "-form").css('display', 'inline');
	fconnection.init();
	connectionTooltips.init(connectionType);
};

<g:each in="${Fconnection.implementations}" var="imp">
	fconnection.${imp.shortName} = {
		<%
			def asJs = { it? '"' + it.join('", "') + '"': '' }
			def nonNullableConfigFields = asJs(Fconnection.getNonnullableConfigFields(imp))
			def validationSubsectionFieldKeys = asJs(imp.configFields instanceof Map? imp.configFields.getAllKeys(): null)
		%>
		requiredFields: [${nonNullableConfigFields}],
		validationSubsectionFieldKeys: [${validationSubsectionFieldKeys}],
		humanReadableName: "<g:message code="${imp.simpleName.toLowerCase()}.label"/>",
		show: function() {
			<g:each in="${(Fconnection.implementations - imp)*.shortName}">
				$("#${it}-confirm").hide();
			</g:each>
			var validationSubsectionFieldKeys = fconnection[fconnection.getType()].validationSubsectionFieldKeys;
			if(validationSubsectionFieldKeys.length > 1) {
				$.each(validationSubsectionFieldKeys, function(index, value) {
					setConfirmation(value);
				});
			}
			<g:set var="configFields" value="${imp.configFields instanceof Map? (imp.configFields.getAllValues()) : imp.configFields}" />
			<g:each in="${configFields}" var="f">
				<g:if test="${f in imp.passwords}">setSecretConfirmation('${f}');</g:if>
				<g:else>setConfirmation('${f}');</g:else>
			</g:each>
			$("#${imp.shortName}-confirm").show();
		}
	};
</g:each>

function initializePopup() {
	fconnection.validator();
	<g:if test="${!fconnectionInstance}">
		fconnection.setType("${fconnectionInstance?fconnectionInstance.getClass().shortName: 'smslib'}");
	</g:if>
	
	fconnection.init();
	$("#tabs").bind("tabsshow", fconnection.show);
	fconnection.attachCheckBoxListener();
	$("#tabs-2").contentWidget({
		validate: fconnection.isValid
	});
}
</r:script>

