<div id="tabs-5">
	<h3>Edit message to be sent to recipients</h3>
	<p>The following message will be sent to the recipients of the poll. This message can be edited before sending.</p>
	<g:textArea name="message" value="" />
</div>
<g:javascript>
	function updateSendMessage() {
		var questionText = $("#question").val() + "?";
		var choicesText = '';
		var keywordText = '';
		var replyText = '';
		if (!$('#poll-keyword').hasAttr("disabled")) {
			choicesText = 'Reply';
			keywordText = $("#poll-keyword").val().toUppercase();
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
		var sendMessage = questionText + ' ' + replyText;
		$("input[name='message']").val(sendMessage);
	}
</g:javascript>