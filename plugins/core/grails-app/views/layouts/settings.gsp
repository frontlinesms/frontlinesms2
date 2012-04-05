<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<title><g:layoutTitle default="Settings"/></title>
		<g:layoutHead />
		<r:require module="settings"/>
		<g:render template="/includes"/>
		<g:javascript>
			$(function() {
				<g:if test="${params.createRoute && params.controller == 'connection'}">
					var connectionTimer = setInterval(refreshConnectionStatus, 2000);
					function refreshConnectionStatus() {
						$.get("${createLink(controller:'connection', action:'list', id:params?.id)}", function(data) {
								$("#connections").replaceWith($(data).find('#connections'));
						});
					}
				</g:if>
			});
		</g:javascript>
	</head>
	<body id="settings-tab">
		<div id="header">
			<div id="notifications">
				<g:render template="/system_notifications"/>
				<g:render template="/flash"/>
			</div>
			<g:render template="/system_menu"/>
			<g:render template="/tabs"/>
		</div>
        <div id="main">
			<g:render template="/settings/menu"/>
			<div id="content">
				<div class="section-header">
					<h3 class="settings"><g:message code="layout.settings.header" /></h3>
				</div>
				<g:layoutBody />
			</div>
		</div>
		<r:layoutResources/>
	</body>
</html>
