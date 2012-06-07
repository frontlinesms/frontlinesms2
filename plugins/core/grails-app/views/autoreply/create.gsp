<meta name="layout" content="popup"/>
<div id="tabs" class="vertical-tabs">
	<div class="error-panel hide"><div id="error-icon"></div><g:message code="autoreply.validation.prompt"/></div>
	<ol>
		<li><a class="tabs-1" href="#tabs-1"><g:message code="autoreply.enter.keyword"/></a></li>
		<li><a class="tabs-2" href="#tabs-2"><g:message code="autoreply.create.message"/></a></li>
		<li><a class="tabs-3" href="#tabs-3"><g:message code="autoreply.confirm"/></a></li>
	</ol>
	<g:formRemote name="create_autoreply" url="[action:'save', controller:'autoreply', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]" method="post"  onSuccess="checkForSuccessfulSave(data, i18n('autoreply.label'))">
		<fsms:wizardTabs templates="
				/autoreply/keyword,
				/message/compose,
				/autoreply/confirm,
				/autoreply/save"/>
	</g:formRemote>
</div>
<r:script>
	function initializePopup() {
		$("#messageText").trigger("keyup");
		
		$("#tabs-1").contentWidget({
			validate: function() {
				if ((isElementEmpty("#tabs-1 #keyword"))&&(!(isGroupChecked("blankKeyword")))) {
					$("#tabs-1 #keyword").addClass("error");
					return false;
				}
				return true;
			}
		});
		
		$("#tabs-2").contentWidget({
			validate: function() {
				if (isElementEmpty("#tabs-2 #messageText")) {
					$("#tabs-2 #messageText").addClass("error");
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
		if(!(isGroupChecked("blankKeyword"))){
			var keyword = $('#keyword').val().toUpperCase();
			var autoreplyText = $('#messageText').val();

			$("#keyword-confirm").html('<p>' + keyword  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		}
		else{
			var autoreplyText = $('#messageText').val();
			$("#keyword-confirm").html('<p>' + i18n("autoreply.blank.keyword")  + '</p>');
			$("#autoreply-confirm").html('<p>' + autoreplyText  + '</p>');
		}
		
	}
</r:script>
