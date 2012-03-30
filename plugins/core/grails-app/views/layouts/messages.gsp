<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<r:require module="messages"/>
		<g:render template="/includes" plugin="core"/>
		<g:javascript>
			$(function() {  
			   disablePaginationControls();
			});
		</g:javascript>
	</head>
	<body id="messages-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications" plugin="core"/>
				<g:render template="/flash" plugin="core"/>
			</div>
			<g:render template="/system_menu" plugin="core"/>
			<g:render template="/tabs" plugin="core"/>
		</div>
		<div id="main">
			<g:render template="../message/menu" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
			<div id="content">
				<g:render template="../message/header" plugin="core"/>
				<g:render template="../message/message_list" plugin="core"/>
				<g:layoutBody/>
			    <g:render template="../message/footer" plugin="core"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
