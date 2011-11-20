<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Help"/></title>
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
		<g:javascript src="application.js"/>
	</head>
	<body>
		<g:render template="/system_notifications"/>
		<div id="container">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
	        <g:render template="/flash"/>
	        <div class="main">
				<div class="content">
					<div class="content-header">
						<div class="help-title">
							<h2>Help</h2>
						</div>
					</div>
					<div class="content-body">
						<g:layoutBody />
					</div>
					<div class="content-footer">
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
