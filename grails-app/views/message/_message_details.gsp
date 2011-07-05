	<div id="message-details">
		<p class="message-name">${messageInstance.displaySrc}</p>
		<g:def var="thisAddress" value="${messageInstance.src}"/>
		<g:if test="${!messageInstance.contactExists}">
			<g:link class="button" controller="contact" action="createContact" params="[primaryMobile: thisAddress]">+</g:link>
		</g:if>
		<p class="message-date"><g:formatDate format="dd-MMM-yyyy hh:mm" date="${messageInstance.dateCreated}" /></p>
		<p class="message-body">${messageInstance.text}</p>
		<div class="buttons">
			${buttons}
		</div>
	</div>
	<g:render template="action_list"/>
