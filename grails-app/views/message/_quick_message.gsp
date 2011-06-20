<%@ page contentType="text/html;charset=UTF-8" %>
<script>
	function loadContents(data) {
		$(data).dialog({title: "Quick Message", width: 600})
	}
</script>
<g:remoteLink action="quick_message_popup.gsp" onSuccess="loadContents(data);" class="quick_message">
	Quick Message
</g:remoteLink>
