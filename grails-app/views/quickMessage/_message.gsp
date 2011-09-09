<div id="tabs-1" class="${configureTabs.contains('tabs-1') ? '' : 'hide'}">
	<label class="header" for="messageText">Enter message</label><br />
	<g:textArea name="messageText" value="${messageText}" rows="5" cols="40"/>
</div>

<script>
	$("#messageText").live("blur", function() {
		var value = $(this).val();
		if(value) {
			$("#confirm-message-text").html(value)
		}
		else {
			$("#confirm-message-text").html("none")
		}
	})
</script>