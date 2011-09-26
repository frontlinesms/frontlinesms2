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
			// alert("Executing this new bit of javascript...");

			// Update the list of detected devices
			$(document).everyTime(10000, function() {
				// alert("Fetching detected devices update...");
				$.get(url_root + 'status/listDetected',
						function(data) {
							// alert("Got list of detected devices: " + data);
							$('#device-detection').replaceWith($(data));
						});
			});
		</g:javascript>
	</body>
</html>