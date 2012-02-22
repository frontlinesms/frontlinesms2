<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings" />
		<title>Settings > Connections > ${connectionInstance?.name}</title>
		<g:javascript>
			setInterval(refreshSystemNotifications, 10000);
			function refreshSystemNotifications() {
				var notificationsHolder = $("#notifications")
				$.getJSON("${createLink(controller:'systemNotification', action:'list')}", function(data) {
					var shouldRefresh = false
					$.each(data, function(key, notification) {
						var systemNotification = "<div class='system-notification'>" + notification.text + notification.markRead + "</div>";
						var isDisplayed = false
						
						notificationsHolder.find('div').each(function() {
							var textB = trim($(systemNotification).html())
							var textA = trim($(this).html())
							if(textA.indexOf(textB) != -1) {
								isDisplayed = true
							}
						});
						
						if(!isDisplayed) {
							notificationsHolder.append(systemNotification);
							if(notification.text.indexOf("Created") != -1) {
								shouldRefresh = true
							}
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
			
			function trim(str) {
				return str.replace(/^\s+|\s+$/g,"");
			}

		</g:javascript>
	</head>
	<body>
		<g:render template="/connection/connection_list"/>
	</body>
</html>
