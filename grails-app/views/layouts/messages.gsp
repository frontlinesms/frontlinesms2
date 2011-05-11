<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
        <g:layoutHead />
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'grails-default.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
	</head>
	<body>
		<g:render template="/eyebrow"/>
		<g:if test="${flash.message}">
			<div class="message">${flash.message}</div>
		</g:if>
		<g:hasErrors bean="${messageInstance}">
			<div class="errors">
				<g:renderErrors bean="${messageInstance}" as="list"/>
			</div>
		</g:hasErrors>
		<g:render template="menu"/>
		<g:render template="message_list"/>
		<g:if test="${messageInstance != null}">
			<div id="message-details">
				<p>${messageInstance.src}</p>
				<p><g:formatDate format="dd-MMM-yyyy hh:mm" date="${messageInstance.dateCreated}" /></p>
				<p>${messageInstance.text}</p>
			</div>
		</g:if>
		<g:layoutBody />
	</body>
</html>