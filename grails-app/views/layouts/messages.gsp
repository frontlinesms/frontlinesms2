<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
        <g:layoutHead />
		<g:render template="/css"/>
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

<!--	Compass Stylesheets	-->
		<link href="${resource(dir:'css',file:'screen.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
		<link href="${resource(dir:'css',file:'print.css')}" media="print" rel="stylesheet" type="text/css" />
		<!--[if lt IE 8]>
		  <link href="${resource(dir:'css',file:'ie.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
		<![endif]-->


		<g:javascript library="jquery" plugin="jquery"/>
	</head>
	<body>
		<g:render template="/eyebrow"/>
		<g:render template="/flash"/>
		<div id="main">
			<g:render template="menu"/>
			<g:render template="message_list"/>
			<g:if test="${messageInstance != null}">
				<div id="message-details">
					<p class="contact-name">${messageInstance.src}</p>
					<p class="message-date"><g:formatDate format="dd-MMM-yyyy hh:mm" date="${messageInstance.dateCreated}" /></p>
					<p class="message-body">${messageInstance.text}</p>
				</div>
			</g:if>
			<g:layoutBody/>
		</div>
	</body>
</html>