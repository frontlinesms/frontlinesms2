<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > Connections > ${connectionInstance?.name}</title>
		<g:javascript>
			setInterval(refreshSystemNotifications, 10000);
			function refreshSystemNotifications() {
				var notificationIds = ""
				var indx = "notification-".length
				$("#notifications").find('div:visible').each(function() {
					notificationIds += $(this).attr("id").substring(indx) + ","
				});
				
				$.getJSON("${createLink(controller:'systemNotification', action:'list')}", {notificationIdList:notificationIds}, function(data) {
					var shouldRefresh = false
					if(data != null) {
						$.each(data, function(key, notification) {
							var systemNotification = "<div class='system-notification' id='notification-'" + notification.id +">" + notification.text + notification.markRead + "</div>";
							$("#notifications").append(systemNotification);
							if(notification.text.indexOf("Created") != -1) {
								shouldRefresh = true
								}
						});
		
						if(shouldRefresh) {
							reloadConnectionList()
						}
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
