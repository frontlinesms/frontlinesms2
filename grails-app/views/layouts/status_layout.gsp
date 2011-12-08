<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Status"/></title>
		<g:layoutHead />
		<g:javascript src="jquery.timers.js"/>
		<g:javascript src="jquery.ui.selectmenu.js"/>
		<g:javascript src="application.js"/>
		<g:render template="/css"/>
		<g:javascript src="mediumPopup.js"/>
		<g:javascript src="smallPopup.js"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
	</head>
	<body id="status-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications"/>
				<g:render template="/flash"/>
			</div>
			<img id="logo" src="/frontlinesms2/images/logo.png">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
		</div>
		<div id="main">
			<div class="content">
				<g:layoutBody />
			</div>
		</div>
	</body>
</html>
