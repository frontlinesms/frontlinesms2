<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="status_layout"/>
		<title><g:message code="status.header"/></title>
		</head>
	<body>
		<div id="left-column">
			<fsms:render template="traffic"/>
		</div>
		<div id="right-column">
			<fsms:render template="connection_list"/>
			<fsms:render template="device_detection"/>
			
		</div>
		<r:script>
			// TODO should this be bind()/on() instead of delegate?
			$("#time-filters").delegate("select", "change", function() {
				$('input[name="rangeOption"]').prop('checked', true);
			});
		</r:script>
	</body>
</html>
