<%@ page contentType="text/html;charset=UTF-8" %>
<script>
	function loadContents(data) {
		$(data).dialog({title: "Quick Message", width: 600})
		$("#tabs").tabs();
	}
	
	function moveToTabBy(index) {
		var tabWidget = $('#tabs').tabs();
		var selected = tabWidget.tabs('option', 'selected')
		tabWidget.tabs('select', selected + index);
		return false;
	}
	
	$('.next').live('click', function() {
		return moveToTabBy(1);
	});
	
	$('.back').live('click', function() {
		return moveToTabBy(-1);
	});

	$('.add-address').live('click', function() {
		var address = $('#address').val();
		$("#contacts").prepend("<div><input type='checkbox' checked='true' name='addresses' value=" + address + ">" +  address + "</input></div>")
	});

</script>
<g:remoteLink controller="quickMessage" action="create" onSuccess="loadContents(data);" class="quick_message">
	Quick Message
</g:remoteLink>
