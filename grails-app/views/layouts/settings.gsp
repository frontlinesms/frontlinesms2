<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<g:javascript src="jquery.timers.js"/>
		<g:javascript src="jquery.ui.selectmenu.js"/>
		<g:javascript src="application.js"/>
	</head>
	<body id="settings-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications"/>
				<g:render template="/flash"/>
			</div>
			<img src='${resource(dir:'images',file:'logo.png')}' id="logo"/>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
		</div>
        <div id="main">
			<g:render template="menu"/>
			<div id="content">
				<div class="section-header">
					<h3>Settings</h3>
				</div>
				<g:layoutBody />
			</div>
		</div>
	</body>
</html>
