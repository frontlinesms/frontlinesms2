<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript library="jquery" plugin="jquery"/>
<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
<script type="text/javascript">
	url_root = "${request.contextPath}/";
	
	function setChecked(connectionType) {
		$("#type-list input[checked=checked]").attr('checked', '');
		$("#type-list ." + connectionType).attr('checked', 'checked');
		$("#smslib-form").css('display', 'none');
		$("#email-form").css('display', 'none');
		$("#" + connectionType + "-form").css('display', 'inline');
	}
</script>
<g:javascript src="application.js"/>
<g:javascript src="mediumPopup.js"/>
<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div>Please fill in all required fields</div>
	<ol>
		<g:if test="${!fconnectionInstance}">
			<li><a href="#tabs-1">Choose type</a></li>
		</g:if>
		<li><a href="#tabs-2">Enter details</a></li>
		<li><a href="#tabs-3">Confirm</a></li>
	</ol>
	<g:form name="connectionForm" url="[controller:'connection', action:action, id:fconnectionInstance?.id]" action="${action}" id='${fconnectionInstance?.id}' >
		<g:render template="type"/>
		<g:render template="details"/>
		<g:render template="confirm"/>
	</g:form>
</div>
<g:javascript>
function initializePopup() {
	<g:if test="${fconnectionInstance}">
		setChecked("${fconnectionInstance instanceof frontlinesms2.EmailFconnection ? 'email':'smslib'}");
	</g:if>
	$("#tabs").bind("tabsshow", function(event, ui) {
		updateConfirmationMessage();
	});

	$("#tabs-2").contentWidget({
		validate: function() {
			var tabIsValid
			if($("#type-list").find("input[checked=checked]").val() == 'smslib')
				tabIsValid = !(isElementEmpty('#name')) && !(isElementEmpty('#port'));
			else if($("#type-list").find("input[checked=checked]").val() == 'email')
				tabIsValid = !(isElementEmpty('#email-name')) && ($("#receiveProtocol").val() != 'null') && !(isElementEmpty('#serverName')) && !(isElementEmpty('#serverPort')) && !(isElementEmpty('#username')) && !(isElementEmpty('#password'));
			return tabIsValid;
		}
	});
}

function updateConfirmationMessage() {
	<g:if test="${fconnectionInstance}">
		var type = "${fconnectionInstance instanceof frontlinesms2.EmailFconnection ? 'email':'smslib'}";
	</g:if>
	<g:else>
		var type = $("#type-list").find("input[checked=checked]").val();		
	</g:else>
	
	function setConfirmation(fieldName) {
		var val = $('#' + type + '-form #' + fieldName).val();
		$("#confirm-" + fieldName).text(val);
	}
	function setSecretConfirmation(fieldName) {
		var val = $('#' + type + '-form #' + fieldName).val();
		val = val&&val.length? '****': 'None';
		$("#confirm-" + fieldName).text(val);
	}
	$("#confirm-type").text(type);

	if(type == 'smslib') {
		$("#email-confirm").hide();
		$("#smslib-confirm").show();
		
		setConfirmation('name');
		setConfirmation('port');
		setConfirmation('baud');
		setConfirmation('smsc');
		setConfirmation('imsi');
		setConfirmation('serial');
		setSecretConfirmation('pin');
	} else if (type == 'email') {
		$("#smslib-confirm").hide();
		$("#email-confirm").show();
		
		var name = $('#' + type + '-form #email-name').val();
		$("#confirm-name").text(name);
		setConfirmation('receiveProtocol');
		setConfirmation('serverName');
		setConfirmation('serverPort');
		setConfirmation('username');
		setSecretConfirmation('password');
	}
}
</g:javascript>
