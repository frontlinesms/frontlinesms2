<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Contacts"/></title>
        <g:layoutHead />
		<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'grails-default.css')}" />
		<meta name="layout" content="main" />
	</head>
	<body>
		<g:render template="/eyebrow"/>
		<g:if test="${flash.message}">
			<div class="message">${flash.message}</div>
		</g:if>
		<g:hasErrors bean="${contactInstance}">
			<div class="errors">
				<g:renderErrors bean="${contactInstance}" as="list"/>
			</div>
		</g:hasErrors>
		<g:render template="menu"/>
		<g:render template="contact_list"/>
		<g:layoutBody />
	</body>
</html>