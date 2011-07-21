<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources />
		<g:javascript src="application.js"/>
		<g:javascript src="popup.js" />
		<g:javascript src="/message/move_dropdown.js" />
    </head>
	<body>
		<g:render template="/tabs"/>
		<g:render template="quick_message"/>
		<g:remoteLink controller="quickMessage" action="create" onSuccess="launchWizard('Quick Message', data);" class="quick_message">
			Quick Message
		</g:remoteLink>
        <g:render template="/flash"/>
		<div id="main">
			<g:render template="menu"/>
			<g:render template="message_list"/>
			<g:layoutBody/>
		</div>
	</body>
</html>
