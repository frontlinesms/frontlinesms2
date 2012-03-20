<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript library="jquery" plugin="jquery"/>
<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
<g:javascript>
	url_root = "${request.contextPath}/";
	
	<g:if test="${fconnectionInstance}">
		var fconnectionType = "${fconnectionInstance.getClass().simpleName.toLowerCase() - 'fconnection'}";
	</g:if>
	<g:else>
		var fconnectionType = $("#type-list").find("input[checked=checked]").val();		
	</g:else>
	
	function setConnectionType(connectionType) {
		fconnectionType = connectionType;
		$("#type-list input[checked=checked]").attr('checked', '');
		$("#type-list ." + connectionType).attr('checked', 'checked');
		$("#smslib-form").css('display', 'none');
		$("#email-form").css('display', 'none');
		$("#clickatell-form").css('display', 'none');
		$("#" + connectionType + "-form").css('display', 'inline');
	}
</g:javascript>
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
	<g:form name="connectionForm" url="[controller:'connection', action:action]" action="${action}" id='${fconnectionInstance?.id}' >
		<g:render template="type"/>
		<g:render template="details"/>
		<g:render template="confirm"/>
	</g:form>
</div>
<g:javascript>
function isFieldSet(fieldName) {
	var val = getFieldVal(fieldName);
	return val!=null && val.length>0;
}

function getFieldVal(fieldName) {
	var val = $('#' + fconnectionType + '-form input[name="' + fieldName + '"]').val();
	return val;
}

function setConfirmVal(fieldName, val) {
	$("#" + fconnectionType + "-confirm #confirm-" + fieldName).text(val);
}

function setConfirmation(fieldName) {
	setConfirmVal(fieldName, getFieldVal(fieldName));
}

function setSecretConfirmation(fieldName) {
	val = isFieldSet(fieldName)? '****': 'None';
	setConfirmVal(fieldName, val);
}

function initializePopup() {
	<g:if test="${fconnectionInstance}">
		setConnectionType(fconnectionType);
	</g:if>
	<g:else>
		setConnectionType('smslib');
	</g:else>
	
	$("#tabs").bind("tabsshow", function(event, ui) {
		updateConfirmationMessage();
	});

	$("#tabs-2").contentWidget({
		validate: function() {
			var checked = $("#type-list").find("input[checked=checked]").val();
			if(checked == 'smslib') {
				return isFieldSet('name')
						&& isFieldSet('port');
			} else if(checked == 'email') {
				return isFieldSet('name')
						&& ($("#receiveProtocol").val() != 'null')
						&& isFieldSet('serverName')
						&& isFieldSet('serverPort')
						&& isFieldSet('username')
						&& isFieldSet('password');
			} else if(checked == 'clickatell') {
				return isFieldSet('name')
						&& isFieldSet('apiId')
						&& isFieldSet('username')
						&& isFieldSet('password');
			}
		}
	});
}

function getFconnectionTypeAsHumanReadable() {
	if(fconnectionType == 'smslib') return "${g.message(code:'smslibfconnection.label')}"
	if(fconnectionType == 'clickatell') return "${g.message(code:'clickatellfconnection.label')}"
	if(fconnectionType == 'email') return "${g.message(code:'emailfconnection.label')}"
}

function updateConfirmationMessage() {
	setConfirmVal('type', getFconnectionTypeAsHumanReadable());
	setConfirmation('name');

	if(fconnectionType == 'smslib') {
		$("#email-confirm").hide();
		$("#clickatell-confirm").hide();
		$("#smslib-confirm").show();
		
		setConfirmation('port');
		setConfirmation('baud');
		setConfirmation('smsc');
		setConfirmation('imsi');
		setConfirmation('serial');
		setSecretConfirmation('pin');
	} else if (fconnectionType == 'email') {
		$("#smslib-confirm").hide();
		$("#clickatell-confirm").hide();
		$("#email-confirm").show();
		
		setConfirmation('receiveProtocol');
		setConfirmation('serverName');
		setConfirmation('serverPort');
		setConfirmation('username');
		setSecretConfirmation('password');
	} else if (fconnectionType == 'clickatell') {
		$("#smslib-confirm").hide();
		$("#email-confirm").hide();
		$("#clickatell-confirm").show();
		
		setConfirmation('apiId');
		setConfirmation('username');
		setSecretConfirmation('password');
	}
}
</g:javascript>
