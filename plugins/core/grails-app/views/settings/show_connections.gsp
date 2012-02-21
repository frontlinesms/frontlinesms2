<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > Connections > ${connectionInstance?.name}</title>
		<g:javascript>
			setInterval(reloadConnectionList, 10000);
			function refreshSystemNotifications() {
				var notificationsHolder = $("#notifications")
				notificationsHolder.empty()
				$.getJSON("${createLink(controller:'systemNotification', action:'list')}", function(data) {
					var shouldRefresh = false
					$.each(data, function(key, notification) {
						var systemNotification = "<div class='system-notification'>" + notification.text + notification.markRead + "</div>";
						notificationsHolder.append(systemNotification);
						if(notification.text.indexOf("Created") != -1) {
							shouldRefresh = true
						}
					});
		
					if(shouldRefresh) {
						reloadConnectionList()
					}
				});
			}

			function reloadConnectionList() {
				var link = "${createLink(controller:'settings', action:'connections', id:params.id)}"
				$.get(link, function(data) {
					$('#connections').replaceWith($(data).find('#connections'));
				});
			}
		</g:javascript>
	</head>
	<body>
		<g:render template="/connection/connection_list"/>
	</body>
</html>
