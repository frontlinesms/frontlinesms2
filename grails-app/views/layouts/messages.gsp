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
		<div id="container">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
	        <g:render template="/flash"/>
	        <div class="main">
		        <div class="content-header">
			        <g:remoteLink controller="quickMessage" action="create" onSuccess="launchWizard('Quick Message', data);" class="quick_message">
						Quick Message
					</g:remoteLink>
				</div>
				<g:render template="menu"/>
				<div class="content">
					<div class="content-body">
						<g:render template="message_list"/>
						<g:layoutBody />
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
