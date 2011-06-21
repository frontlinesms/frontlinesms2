<%@ page contentType="text/html;charset=UTF-8" %>
<script>
	function loadContents(data) {
		$(data).dialog({title: "Quick Message", width: 600})
		$("#tabs").tabs();
		$('.next').bind('click', function() {
			var tabWidget = $('#tabs').tabs();
			var selected = tabWidget.tabs('option', 'selected')
			tabWidget.tabs('select', selected + 1);
			return false;
		});
	}
</script>
<g:remoteLink action="quick_message_popup.gsp" onSuccess="loadContents(data);" class="quick_message">
	Quick Message
</g:remoteLink>
