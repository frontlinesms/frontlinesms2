<%@ page contentType="text/html;charset=UTF-8"; import="frontlinesms2.ConnectionStatus"%>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="connection.header"/> ${connectionInstance?.name}</title>
		<g:if test="${params.connecting}">
			<r:script>
				$(function() {
					var connectionTimer = setInterval(refreshConnectionStatus, 2000);
					function refreshConnectionStatus() {
						$.get("${createLink(controller:'connection', action:'list', id:params?.id, params:[format:'json'])}", function(connection) {
							var status;
							status = connection.status.substring(17).toUpperCase();
							if(status !== "CONNECTING") {
								clearInterval(connectionTimer);
								$("div.flash").hide();
								fconnection_show.update(status, connection.id);
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
