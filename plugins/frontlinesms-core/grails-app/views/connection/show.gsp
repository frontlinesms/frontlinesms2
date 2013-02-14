<%@ page contentType="text/html;charset=UTF-8"; import="frontlinesms2.ConnectionStatus"%>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="connection.header"/> ${connectionInstance?.name}</title>
		<g:if test="${params.createRoute}">
			<r:script>
				$(function() {
					app_info.listen("connection_show", { id:${params.id} }, function(data) {
						console.log("connection_show.callback :: data=" + JSON.stringify(data));
						var c;
						if(!data.connection_show) return;
						c = data.connection_show;
						if(c.status !== "CONNECTING") {
							app_info.stopListening("connection_show");
							$("div.flash").hide();
							fconnection_show.update(c.status, c.id);
						}
					});
				});
			</r:script>
		</g:if>
	</head>
	<body>
		<fsms:render template="/connection/connection_list"/>
	</body>
</html>
