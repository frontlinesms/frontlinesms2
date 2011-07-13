<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Trash</title>
		<script>
			function emptyTrash() {
				$('<div><p>This will empty trash and delete messages permanently.</p>Do you want to continue?</div>').dialog({
					modal: true,
					title: "Empty Trash?",
					width: 600,
					buttons:{
						"Yes": function() {
							window.location = 'emptyTrash';
						},
						"No" : function() { 
							$(this).dialog("close");
						}
					}
				});
			}
		</script>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
		 	<a href="#" onClick="emptyTrash()" id="empty-trash">Empty trash</a>
			<g:render template="message_details" />
		</g:if>
    </body>
</html>