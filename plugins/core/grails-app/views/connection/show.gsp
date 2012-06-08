<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="connection.header"/> ${connectionInstance?.name}</title>
		<g:if test="${params.createRoute}">
			<r:script>
				$(function() {
					var count = 0;
					var oldSystemNotificationCount = $("div.system-notification").length;
					var connectionTimer = setInterval(refreshConnectionStatus, 2000);
					function refreshConnectionStatus() {
						$.get("${createLink(controller:'connection', action:'list', id:params?.id)}", function(data) {
							var newSystemNotificationCount = $("div.system-notification").length;
							if (count < 2 && oldSystemNotificationCount == newSystemNotificationCount) {
								count++;
								$(".connections").replaceWith($(data).find('.connections'));	
							} else {
								clearInterval(connectionTimer);
								$("div.flash").hide();
							}	
						});
					}
				});
			</r:script>
		</g:if>
	</head>
	<body>
		<fsms:render template="/connection/connection_list"/>
	</body>
</html>
