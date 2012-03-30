<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > Connections > ${connectionInstance?.name}</title>
		<g:javascript>
			setInterval(refreshSystemNotifications, 10000);
			
			function refreshSystemNotifications() {
				var previousNotificationCount = $("#notifications").find("div:visible").length
				$.get("${createLink(controller:'systemNotification', action:'list')}", function(data) {
					var newNotificationCount = $(data).find("a").length
					if(newNotificationCount > 0) {
						$("#notifications").empty().append(data);
						if($(data).text().indexOf("Created") != -1) {
							reloadConnectionList();
		                }
		                removeConnectingNotification();
					}
					
				});
			}

			function reloadConnectionList() {
				var link = "${createLink(controller:'settings', action:'connections', id:params.id)}";
				$.get(link, function(data) {
					$('#connections').replaceWith($(data).find('#connections'));
				});
			}
			
			function removeConnectingNotification() {
				$('#notifications div').remove('.flash');
				updateCreateRouteLabel();
			}
			
			function updateCreateRouteLabel() {
				if($('#notifications div.flash:contains("${message(code: 'connection.route.connecting')}")').text().indexOf("${message(code: 'connection.route.connecting')}") != -1) {
					$("#createRoute a").text("${message(code: 'connection.route.connecting')}");
					$("#createRoute a").attr("disabled", "disabled");
				} else {
					$("#createRoute a").text("Create route");
					$("#createRoute a").attr("disabled", "");
				}
			}

		</g:javascript>
	</head>
	<body>
		<g:render template="/connection/connection_list" plugin="core"/>
	</body>
</html>
