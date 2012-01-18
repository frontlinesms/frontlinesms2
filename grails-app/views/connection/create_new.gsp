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
		<li><a href="#tabs-1">Choose type</a></li>
		<li><a href="#tabs-2">Enter details</a></li>
		<li><a href="#tabs-3">Confirm</a></li>
	</ol>

	<g:form action="save" id='newConnection'>
		<g:render template="type"/>
		<g:render template="details"/>
		<g:render template="confirm"/>
	</g:form>
</div>
<script>
function initializePopup() {
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
	var type = $("#type-list").find("input[checked=checked]").val();
	if(type == 'smslib') {
		$("#email-confirm").hide();
		$("#smslib-confirm").show();
		var name = $('#' + type + '-form #name').val();
		var port = $('#' + type + '-form #port').val();
		var baud = $('#' + type + '-form #baud').val();
		$("#confirm-name").text(name);
		$("#confirm-type").text('smslib');
		$("#confirm-port").text(port);
		$("#confirm-baud").text(baud);
		if ($('#' + type + '-form #pin').val())
			$("#confirm-pin").text('****');
		else
			$("#confirm-pin").text('None');
		
	} else if (type == 'email') {
		$("#smslib-confirm").hide();
		$("#email-confirm").show();
		var name = $('#' + type + '-form #email-name').val();
		var receiveProtocol = $('#' + type + '-form #receiveProtocol').val();
		var serverName = $('#' + type + '-form #serverName').val();
		var serverPort = $('#' + type + '-form #serverPort').val();
		var username = $('#' + type + '-form #username').val();
		$("#confirm-name").text(name);
		$("#confirm-type").text('email');
		$("#confirm-protocol").text(receiveProtocol);
		$("#confirm-server-name").text(serverName);
		$("#confirm-server-port").text(serverPort);
		$("#confirm-username").text(username);
		if ($('#' + type + '-form #password').val() && $('#' + type + '-form #password').val() != '') {
			var passwordLength = $('#' + type + '-form #password').val().length;
			var passwordAsterics = '*';
			var i = 1;
			for(i = 1; i < passwordLength; i++)
				passwordAsterics += '*';
			$("#confirm-password").text(passwordAsterics);
		} else
			$("#confirm-password").text('None');
	}
}
</script>