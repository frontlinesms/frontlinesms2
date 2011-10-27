<div id="tabs-5">
	<h3>Edit message to be sent to recipients</h3>
	<p>The following message will be sent to the recipients of the poll. This message can be edited before sending.</p>
	<g:textArea name="messageText" rows="5" cols="40" />
	<span id="message-stats">0 characters (1 SMS message)</span>
</div>
<g:javascript>
	var autoUpdate = true;
	$("#messageText").live("keyup", updateCount);
	
	function updateSendMessage() {
		$("#messageText").live("keypress", autoUpdateOff);
		$(".choices").live("keypress", autoUpdateOn);
		$("#question").live("keypress", autoUpdateOn);
		
		if(autoUpdate) {
			var questionText = $("#question").val();
			if (questionText.substring(questionText.length - 1) != '?') questionText = questionText + '?';
			questionText = questionText + '\n';
			var choicesText = '';
			var keywordText = '';
			var replyText = '';
			if ($('#poll-keyword').attr("disabled") == undefined || $('#poll-keyword').attr("disabled") == false) {
				choicesText = 'Reply';
				keywordText = $("#poll-keyword").val().toUpperCase();
				if($("input[name='poll-type']:checked").val() == "standard") {
					replyText = choicesText + ' "' + keywordText + ' A" for Yes, "' + keywordText + ' B" for No.';
				} else {
					replyText = choicesText;
					$(".choices").each(function() {
						if (replyText != choicesText && this.value) replyText = replyText + ',';
						if (this.value) replyText = replyText + ' "' + keywordText + ' ' + this.name.substring(6,7) + '" for ' + this.value;
					});
					replyText = replyText + '.';
				}
			}
			var sendMessage = questionText + replyText;
			$("#messageText").val(sendMessage);
			$("#messageText").keyup();
		}
	}
	
	function autoUpdateOff() {
		autoUpdate = false;
	}
	
	function autoUpdateOn() {
		autoUpdate = true;
	}
</g:javascript>