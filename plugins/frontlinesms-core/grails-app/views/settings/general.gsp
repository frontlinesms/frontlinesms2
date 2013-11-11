<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="settings.general.header"/></title>
		<export:resource/>
	</head>
	<body>
		<div id="body-content-head">
			<h1><g:message code="layout.settings.header"/></h1>
		</div>
		<div id="body-content">
			<fsms:render template="/settings/sections/language"/>
			<fsms:render template="/settings/sections/database_backup"/>
			<fsms:render template="/settings/sections/basic_auth"/>
		</div>
	</body>
</html>

<r:script>
$(function() {
	basicAuthValidation.enable();
	$("#basic-authentication input[name=enabled]").attr("onchange", "basicAuthValidation.toggleFields(this)");
	$("#basic-authentication input[type=submit]").attr("onclick", "basicAuthValidation.showErrors()");
});
</r:script>

