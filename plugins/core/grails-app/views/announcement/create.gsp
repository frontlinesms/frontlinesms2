<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>

<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div>Please fill in all required fields</div>
	<ol>
		<li><a class="tabs-1" href="#tabs-1">Enter message</a></li>
		<li><a class="tabs-2" href="#tabs-2">Select recipients</a></li>
		<li><a class="tabs-3" href="#tabs-3">Confirm</a></li>
	</ol>

	<g:formRemote name="create_announcement" url="${[action:'save', controller:'announcement']}" method="post"  onSuccess="launchMediumPopup('Announcement created!', data, 'OK', summaryRedirect)">
		<g:render template="message"/>
		<div id="tabs-2">
			<g:render template="../quickMessage/select_recipients" model= "['contactList' : contactList,
			                                                                'groupList': groupList,
			                                                                'nonExistingRecipients': [],
			                                                                'recipients': []]"/>
		</div>
		<g:render template="confirm"/>
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
		
	function summaryRedirect() {
		var ownerId = $(".summary #ownerId").val();
		$(this).dialog('close');
		window.location.replace(url_root + "message/announcement/" + ownerId);
	}
</g:javascript>
