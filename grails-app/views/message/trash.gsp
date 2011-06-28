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
			<a href="#" onClick="emptyTrash()" >Empty trash</a>
			<div id="message-details">
				<p class="message-name">${messageInstance.displaySrc}</p>
				<g:def var="thisAddress" value="${messageInstance.src}"/>
				<g:if test="${!messageInstance.contactExists}">
					<g:link class="button" controller="contact" action="createContact" params="[address: thisAddress]">+</g:link>
				</g:if>
				<p class="message-date"><g:formatDate format="dd-MMM-yyyy hh:mm" date="${messageInstance.dateCreated}" /></p>
				<p class="message-body">${messageInstance.text}</p>
			</div>
		</g:if>
    </body>
</html>