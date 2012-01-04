<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<g:render template="/css" plugin="core"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon"/>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<g:javascript src="jquery.timers.js" plugin="core"/>
		<g:javascript src="jquery.ui.selectmenu.js" plugin="core"/>
		<g:javascript src="application.js" plugin="core"/>
		<g:javascript src="mediumPopup.js" plugin="core"/>
		<g:javascript src="smallPopup.js" plugin="core"/>
		<g:javascript src="pagination.js" plugin="core"/>
		<g:javascript src="/message/check_message.js" plugin="core"/>
		<g:javascript src="/message/arrow_navigation.js" plugin="core"/>
		<g:javascript src="/message/star_message.js" plugin="core"/>
		<g:javascript src="/message/messageSorting.js" plugin="core"/>
		<g:javascript src="/message/categorize_dropdown.js" plugin="core"/>
		<g:javascript src="/message/move_dropdown.js" plugin="core"/>
		<g:javascript src="/message/moreActions.js" plugin="core"/>
		<g:javascript src="on_air.js"/>
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
	</body>
</html>
