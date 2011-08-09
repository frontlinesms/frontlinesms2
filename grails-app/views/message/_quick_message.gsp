<%@ page contentType="text/html;charset=UTF-8" %>
<script>
	$('.add-address').live('click', function() {
		var address = $('#address').val();
		$("#contacts").prepend("<div><input type='checkbox' checked='true' name='addresses' value=" + address + ">" +  address + "</input></div>")
		updateCount()
	});

</script>
