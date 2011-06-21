<%@ page contentType="text/html;charset=UTF-8" %>
<script>
	function loadContents(data) {
		$(data).dialog({title: "Quick Message", width: 600})
		$("#tabs").tabs();
	}
	$('.next').live('click', function() {
		var tabWidget = $('#tabs').tabs();
		var selected = tabWidget.tabs('option', 'selected')
		tabWidget.tabs('select', selected + 1);
		return false;
	});
	
	$('.add-address').live('click', function() {
		var address = $('#address').val();
		$("#contacts").prepend("<div><input type='checkbox' checked='true' name='contact' value=" + address + ">" +  address + "</input></div>")
	});

</script>
<g:remoteLink controller="quickMessage" action="create" onSuccess="loadContents(data);" class="quick_message">
	Quick Message
</g:remoteLink>
