<%@ page contentType="text/html;charset=UTF-8"; import="frontlinesms2.ConnectionStatus"%>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="connection.header"/> ${connectionInstance?.name}</title>
		<g:if test="${params.createRoute}">
			<r:script>
				$(function() {
					var count = 0;
					var connectionTimer = setInterval(refreshConnectionStatus, 2000);
					function refreshConnectionStatus() {
						$.get("${createLink(controller:'connection', action:'list', id:params?.id, params:[format:'json'])}", function(connection) {
							if (count < 2 && connection.status == i18n("connectionstatus.connecting")) {
								count++;	
							} else {
								clearInterval(connectionTimer);
								$("div.flash").hide();
								$("#connection-" + connection.id).find(".connection-status").text(i18n(connection.status));
								if(!$(".controls").find("a").is(":visible")) window.location = window.location;
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
