<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="messages" />
        <title>Trash</title>
    </head>
    <body>
		<g:if test="${messageInstance != null}">
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