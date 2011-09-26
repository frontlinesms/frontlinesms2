<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Status"/></title>
		<g:layoutHead />
		<g:render template="/css"/>
		<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
		<g:javascript library="jquery" plugin="jquery"/>
		<script type="text/javascript">
			url_root = "${request.contextPath}/";
			refresh_rate = ${params.rRate ?: 30000}
		</script>
		<g:javascript src="jquery.timers.js"/>
		<g:javascript src="application.js"/>
		<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
	</head>
	<body>
		<div><h1>LOOKS LIKE SOMETHING IS MESSED</h1></div>
		<div id="container">
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
			<g:render template="/flash"/>
			<div class="main">
				<div class="content">
					<div class="content-body">
						<g:layoutBody />
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
