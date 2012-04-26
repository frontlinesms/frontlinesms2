<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>

<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="announcement.validation.prompt" /></div>
	<ol>
		<li><a class="tabs-1" href="#tabs-1"><g:message code="announcement.create.message" /></a></li>
		<li><a class="tabs-2" href="#tabs-2"><g:message code="announcement.select.recipients" /></a></li>
		<li><a class="tabs-3" href="#tabs-3"><g:message code="announcement.confirm" /></a></li>
	</ol>

	<g:formRemote name="create_announcement" url="${[action:'save', controller:'announcement']}" method="post"  onSuccess="checkForSuccessfulSave(data, i18n('announcement.label'))">
		<g:render template="message" plugin="core"/>
		<div id="tabs-2">
			<g:render template="../quickMessage/select_recipients" plugin="core" model= "['contactList' : contactList,
			                                                                'groupList': groupList,
			                                                                'nonExistingRecipients': [],
			                                                                'recipients': []]"/>
		</div>
		<g:render template="confirm" plugin="core"/>
	</g:formRemote>
</div>
<g:javascript>
	function initializePopup() {
		
		$("#tabs-1").contentWidget({
			validate: function() {
				if (isElementEmpty("#tabs-1 #messageText")) {
					$("#tabs-1 #messageText").addClass("error");
					return false;
				}
				return true;
			}
		});
	
		$("#tabs-2").contentWidget({
			validate: function() {
				addAddressHandler();
				return isGroupChecked("addresses")
			}
		});
		
		$("#tabs-3").contentWidget({
			validate: function() {
				$("#tabs-3 #name").removeClass("error");
				var isEmpty = isElementEmpty($("#tabs-3 #name"));
				if(isEmpty) {
					$("#tabs-3 #name").addClass("error");
				}
				return !isEmpty;
			}
		});
		
		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}
	
	function updateConfirmationMessage() {
		var sendMessage = $('#messageText').val();

		var contactNo = $("#contacts-count").text()
		
		if(contactNo == 0 || isGroupChecked("dontSendMessage")) {
			$("#confirm-recepients-count").addClass("hide")
			$("#no-recepients").removeClass("hide")
		} else {
			$("#confirm-recepients-count").removeClass("hide")
			$("#no-recepients").addClass("hide")
		}
		$("#confirm-message-text").html('<p>' + sendMessage  + '</p>');
	}
</g:javascript>
