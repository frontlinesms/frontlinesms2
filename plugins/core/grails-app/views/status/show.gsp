<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="status_layout" />
	</head>
	<body>
		<g:render template="traffic" plugin="core"/>
		<div id="right-column">
			<g:render template="connection_list" plugin="core"/>
			<h3 id="detection-title"><g:message code="status.devises.header" /></h3>
			<g:render template="device_detection" plugin="core"/>
			
		</div>
		<g:javascript>
			setInterval(refreshDevices, 10000);
			
			function refreshDevices() {
				$.get(url_root + 'status/listDetected', function(data) {
					$('#device-detection').replaceWith($(data));
				});
			}
			
			$("#time-filters").delegate("select", "change", function(){
				$('input[name="rangeOption"]').prop('checked', true);
			});
		</g:javascript>
	</body>
</html>
