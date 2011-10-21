<div id="tabs-1" class="${configureTabs.contains('tabs-1') ? '' : 'hide'}">
	<label class="header" for="messageText">Enter message</label><br />
	<g:textArea name="messageText" value="${messageText}" rows="5" cols="40"/>
	<span id="message-stats">0 characters (1 SMS message)</span> 
</div>

<script>
	$("#messageText").live({
		blur: function() {
			var value = $(this).val();
			if(value) {
				$("#confirm-message-text").html(value);
			}
			else {
				$("#confirm-message-text").html("none");
			}
		},
		keyup: function() {
			var value = $(this).val();
			
			if(value.length > 3000) {
				//prevent addition of new content to message
				$(this).val(value.substring(0, 3000));
				
			} else if(value.length > 140) {
				$.get(url_root + 'message/getSendMessageCount', {message: value}, function(data) {
					$("#message-stats").html(value.length + " characters " + data);
				});
			}
			else {
				$("#message-stats").html(value.length + " characters (1 SMS message)");
			}
		}	
	})
</script>