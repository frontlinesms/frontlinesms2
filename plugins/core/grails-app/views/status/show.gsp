<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="status_layout"/>
	</head>
	<body>
		<fsms:render template="traffic"/>
		<div id="right-column">
			<fsms:render template="connection_list"/>
			<h3 id="detection-title"><g:message code="status.devises.header"/></h3>
			<fsms:render template="device_detection"/>
			
		</div>
		<r:script>
			setInterval(refreshDevices, 10000);
			
			function refreshDevices() {
				$.get(url_root + 'status/listDetected', function(data) {
					$('#device-detection').replaceWith($(data));
				});
			}
			
			$("#time-filters").delegate("select", "change", function(){
				$('input[name="rangeOption"]').prop('checked', true);
			});
		</r:script>
	</body>
</html>
