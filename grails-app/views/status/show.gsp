<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="status_layout" />
	</head>
	<body>
		<g:render template="traffic" />
		<g:render template="connection_list" />
		<g:render template="device_detection"/>

		<g:javascript>
			// Update the list of detected devices
			$(document).everyTime(10000, function() {
				$.get(url_root + 'status/listDetected',
						function(data) {
							$('#device-detection').replaceWith($(data));
						});
			});
		</g:javascript>
	</body>
</html>