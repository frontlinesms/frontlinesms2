<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<g:render template="/includes"/>
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
			<g:render template="../message/menu"/>
			<div id="content">
				<g:render template="header"/>
				<g:render template="../radioShow/message_list"/>
				<g:layoutBody/>
			    <g:render template="/message/footer" plugin="core"/>
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
