<%@ page contentType="text/html;charset=UTF-8"%>
<html>
	<head>
		<title><g:layoutTitle default="Messages"/></title>
		<g:layoutHead/>
		<g:render template="/css" />
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon"/>
		<g:javascript library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<g:javascript src="jquery.timers.js"/>
		<g:javascript src="jquery.ui.selectmenu.js"/>
		<g:javascript src="application.js"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<g:javascript src="pagination.js"/>
		<g:javascript src="/message/check_message.js"/>
		<g:javascript src="/message/arrow_navigation.js"/>
		<g:javascript src="/message/star_message.js" />
		<g:javascript src="/message/messageSorting.js"/>
		<g:javascript src="/message/categorize_dropdown.js"/>
		<g:javascript src="/message/move_dropdown.js"/>
		<g:javascript src="/message/moreActions.js"/>
		<g:javascript>
		$(function() {  
		   disablePaginationControls();
		});
		</g:javascript>
	</head>
	<body id="messages-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications"/>
				<g:render template="/flash"/>
			</div>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
		</div>
		<div id="main">
			<g:render template="menu" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
			<div id="content">
				<g:render template="/message/header"/>
				<g:render template="../message/message_list"/>
				<g:layoutBody/>
			    <g:render template="../message/footer"/>
			</div>
		</div>
	</body>
</html>
