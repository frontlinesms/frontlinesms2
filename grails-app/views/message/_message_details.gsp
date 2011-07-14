<g:hiddenField id="message-src" name="message-src" value="${messageInstance.src}" />
<div id="message-details">
	<p class="message-name">${messageInstance.displaySrc}</p>
	<g:def var="thisAddress" value="${messageInstance.src}" />
	<g:if test="${!messageInstance.contactExists}">
		<g:link class="button" controller="contact" action="createContact" params="[primaryMobile: thisAddress]">+</g:link>
	</g:if>
	<p class="message-date"><g:formatDate format="dd-MMM-yyyy hh:mm" date="${messageInstance.dateCreated}" /></p>
	<p class="message-body" id="message-body">${messageInstance.text}</p>
	<div class="buttons">
		<g:if test="${messageSection != 'trash'}">
			<button id="reply">Reply</button>
			<button id="forward">Forward</button>
			<g:link action="deleteMessage" params="[messageSection: messageSection, ownerId: ownerInstance?.id, messageId: messageInstance.id]">Delete</g:link>
		</g:if>
	</div>
</div>
<g:render template="/message/action_list"/>
<script> 
	$(function() {
		$( "#reply" )
			.button()
			.click(function() {
				alert( "Reply" );
			})
			.next()
				.button( {
					text: false,
					icons: {
						primary: "ui-icon-triangle-1-s"
					}
				})
				.click(function() {
					alert( "Could display a menu to select an action" );
				})
				.parent()
					.buttonset();
	});
</script> 
