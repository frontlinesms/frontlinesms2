<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Trash</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
		 	<g:select id="empty-trash" from="${['Empty trash','Show Recipients']}" noSelection="${['null':'Trash actions...']}"></g:select>
			<g:set var="buttons">
			</g:set>
			<g:render template="message_details" model="${[buttons: buttons]}"/>
			<g:javascript>
				$('#empty-trash').change(function(){
					switch($('#empty-trash option:selected').text()) {
						case 'Empty trash' : emptyTrash(); break;
						case 'Show recipient' : showRecipient(); break;
						default: ;
					}
				});

				function emptyTrash() {
					$("<div>${message(code:"messages.trash.confirmation")}</div>").dialog({
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
			
				function showRecipient() {
					//TODO: Yet to implement
				}
			</g:javascript>
		</g:if>
    </body>
</html>