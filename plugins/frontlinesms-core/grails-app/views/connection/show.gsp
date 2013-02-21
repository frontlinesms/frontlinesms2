<%@ page contentType="text/html;charset=UTF-8"; import="frontlinesms2.ConnectionStatus"%>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="connection.header"/> ${connectionInstance?.name}</title>
		<g:if test="${params.connecting}">
			<%-- Could just replace params.createRoute and params.connecting with a check for connectionInstance.status --%>
			<r:script>
				$(function() {
					app_info.listen("connection_show", { id:${params.id} }, function(data) {
						console.log("connection_show.callback :: data=" + JSON.stringify(data));
						data = data.connection_show;
						if(!data) { return; }
						if(data.status !== "CONNECTING") {
							app_info.stopListening("connection_show");
							$("div.flash").hide();
							fconnection_show.update(data.status, data.id);
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

