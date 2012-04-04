<g:javascript src="characterSMS-count.js"/>

<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="autoreply.validation.prompt" /></div>
	<ol>
		<li><a class="tabs-1" href="#tabs-1"><g:message code="autoreply.enter.keyword" /></a></li>
		<li><a class="tabs-2" href="#tabs-2"><g:message code="autoreply.create.message" /></a></li>
		<li><a class="tabs-3" href="#tabs-3"><g:message code="autoreply.confirm" /></a></li>
	</ol>

	<g:formRemote name="create_autoreply" url="[action:'save', controller:'autoreply', params:[ownerId:activityInstanceToEdit?.id ?: null]]" method="post"  onSuccess="launchMediumPopup('Autoreply ${activityInstanceToEdit ? 'updated': 'created'}!', data, 'OK', summaryRedirect)">
		<g:render template="../autoreply/keyword"/>
		<g:render template="../autoreply/message"/>
		<g:render template="../autoreply/confirm"/>
	</g:formRemote>
</div>
<g:javascript>
	function initializePopup() {
		$("#autoreplyText").trigger("keyup");
		
		$("#tabs-1").contentWidget({
			validate: function() {
				if ((isElementEmpty("#tabs-1 #keyword"))&&(!(isGroupChecked("noKeyword")))) {
					$("#tabs-1 #keyword").addClass("error");
					return false;
				}
				return true;
			}
		});
		
		$("#tabs-2").contentWidget({
			validate: function() {
				if (isElementEmpty("#tabs-2 #autoreplyText")) {
					$("#tabs-2 #autoreplyText").addClass("error");
					return false;
				}
				return true;
			}
		});
		
		$("#tabs").bind("tabsshow", function(event, ui) {
			updateConfirmationMessage();
		});
	}
	
	function updateConfirmationMessage() {
		if(!(isGroupChecked("noKeyword"))){
			var keyword = $('#keyword').val();
			var autoreplyText = $('#autoreplyText').val();

			$("#keyword-confirm").html('<p>' + keyword  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		}
		else{
			var autoreplyText = $('#autoreplyText').val();
			$("#keyword-confirm").html('<p>' + "No keyword specified! A response will be sent to everyone that texts into the system"  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		}
		
	}
		
	function summaryRedirect() {
		var ownerId = $(".summary #ownerId").val();
		$(this).dialog('close');
		window.location.replace(url_root + "message/autoreply/" + ownerId);
	}
</g:javascript>
