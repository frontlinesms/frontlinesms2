<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="status.header"/></title>
		</head>
	<body>
		<div id="body-content-head">
			<div class="content">
				<h1><g:message code="status.header"/></h1>
			</div>
		</div>
		<fsms:render template="traffic"/>
		<r:script>
			// TODO should this be bind()/on() instead of delegate?
			$("#time-filters").delegate("select", "change", function() {
				$('input[name="rangeOption"]').prop('checked', true);
			});
		</r:script>
	</body>
</html>
