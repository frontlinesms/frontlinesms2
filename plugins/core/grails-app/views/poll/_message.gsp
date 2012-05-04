<div id="tabs-5">
	<h2 class="bold"><g:message code="poll.message.edit"/></h2>
	<p class="info"><g:message code="poll.message.prompt"/></p>
	<g:textArea name="messageText" rows="5" cols="40"/>
	<span id="send-message-stats" class="character-count"><g:message code="poll.message.count"/></span>
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
			var keywordText = '';
			var replyText = '';
			if ($('#poll-keyword').attr("disabled") == undefined || $('#poll-keyword').attr("disabled") == false) {
				keywordText = $("#poll-keyword").val().toUpperCase();
				if($("input[name='pollType']:checked").val() == "standard") {
					replyText = i18n("poll.reply.text", keywordText, keywordText);
				} else {
					replyText = 'Reply';
					$(".choices").each(function() {
						if (replyText != 'Reply' && this.value) replyText = replyText + ',';
						if (this.value) replyText = i18n("poll.reply.text1", replyText, keywordText, this.name.substring(6,7), this.value);
					});
					replyText = replyText + '.';
				}
			} else if ($("input[name='pollType']:checked").val() == "standard") {
				replyText = i18n("poll.reply.text2");
			} else {
				replyText = 'Please answer ';
				$(".choices").each(function() {
					if (replyText!='Please answer ' && this.value) replyText += i18n("poll.reply.text3");
					if (this.value) replyText += "'" + this.value + "'";
				});
			} 
			var sendMessage = questionText + replyText;
			$("#messageText").val(sendMessage);
			$("#messageText").keyup()
		}
	}
	
	function autoUpdateOff() {
		autoUpdate = false;
	}
	
	function autoUpdateOn() {
		autoUpdate = true;
	}
</g:javascript>
