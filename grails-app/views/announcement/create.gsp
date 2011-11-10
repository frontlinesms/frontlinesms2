<%@ page contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>

<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a class="tabs-1" href="#tabs-1">Enter message</a></li>
		<li><a class="tabs-2" href="#tabs-2">Select recipients</a></li>
		<li><a class="tabs-3" href="#tabs-3">Confirm</a></li>
		<li class="confirm-tab"><a class="tabs-4" href="#tabs-4"></a></li>
	</ol>

	<g:formRemote name="create_announcement" url="${[action:'save', controller:'announcement']}" method="post"  onSuccess="goToSummaryTab()">
		<div class="error-panel hide">Please fill in all the required fields</div>
		<g:render template="message"/>
		<div id="tabs-2">
			<g:render template="../quickMessage/select_recipients" model= "['contactList' : contactList,
			                                                                'groupList': groupList,
			                                                                'nonExistingRecipients': [],
			                                                                'recipients': []]"/>
		</div>
		<g:render template="confirm"/>
		<g:render template="summary"/>
	</g:formRemote>
</div>
<g:javascript>
	function initializePopup() {
		$("#tabs").tabs("disable", getTabLength());
		
		$("#tabs-1").contentWidget({
			validate: function() {
				if (isElementEmpty($("#tabs-1 #messageText"))) {
					$("#tabs-1 #messageText").addClass("error");
					return false;
				}
				return true;
			}
		});
	
		$("#tabs-2").contentWidget({
			validate: function() {
				return isGroupChecked("addresses")
			}
		});
		
		$("#tabs-3").contentWidget({
			validate: function() {
				$("#tabs-3 #announcement-name").removeClass("error");
				var isEmpty = isElementEmpty($("#tabs-3 #announcement-name"));
				if(isEmpty) {
					$("#tabs-3 #announcement-name").addClass("error");
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
		$("#confirm-message-text").html('<pre>' + sendMessage  + '</pre>');
	}
</g:javascript>