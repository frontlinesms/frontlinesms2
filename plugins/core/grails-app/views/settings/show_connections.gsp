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
					if($(this).attr("id")) {
						notificationIds += $(this).attr("id").substring(indx) + ","					
					}
				});
				
				$.getJSON("${createLink(controller:'systemNotification', action:'list')}", {notificationIdList:notificationIds}, function(data) {
					var shouldRefresh = false
					if(data != null) {
						$.each(data, function(key, notification) {
							var div = jQuery('<div/>', {
								id: "notification-" + notification.id,
								html: notification.text
							});
							var link = $(notification.link).addClass("hide-flash");
							$(div).append(link);
							$(div).appendTo("#notifications");
							if(notification.text.indexOf("Created") != -1) {
								shouldRefresh = true
							}
							removeConnectingNotification()
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
			
			function showConnectingNotification(message) {
				jQuery('<div/>', {
					class:"flash message",
					html: message
				}).appendTo('#notifications');
				$("#createRoute a").text(message)
				$("#createRoute a").attr("disabled", "disabled")
			}
			
			function removeConnectingNotification() {
				$('#notifications div').remove('.flash')
				$("#createRoute a:visible").text("CreateRoute")
				$("#createRoute a").attr("disabled", "")
			}
			
		</g:javascript>
	</head>
	<body>
		<g:render template="/connection/connection_list"/>
	</body>
</html>
