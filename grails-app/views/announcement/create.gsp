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
	</g:formRemote>
	<g:render template="summary"/>
</div>
<g:javascript>
	function initialize() {
		$("#tabs").tabs("disable", getTabLength());
	
	
		$("#tabs-2").contentWidget({
			validate: function() {
				return isGroupChecked("groups") || isGroupChecked("addresses")
			}
		});
		
		$("#tabs-3").contentWidget({
			validate: function() {
				$("#tabs-3 #title").removeClass("error");
				var isEmpty = isElementEmpty($("#tabs-3 #title"));
				if(isEmpty) {
					$("#tabs-3 #title").addClass("error");
				}
				return !isEmpty;
			},
			
			onDone: function() {
				$("#create_announcement").submit();
				return false;
			}
		});
	}
	
	function goToSummaryTab() {
		alert('going');
		$("#tabs").tabs("enable", getTabLength());
		$('#tabs').tabs('select', getCurrentTab() + 1);
	}
</g:javascript>