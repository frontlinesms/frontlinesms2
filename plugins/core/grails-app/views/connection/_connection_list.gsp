<%@ page import="frontlinesms2.RouteStatus" %>
<div id='connections'>
	<g:if test="${fconnectionInstanceTotal==0}">
		<div><g:message code="connection.list.noconnection /></div>
	</g:if>
	<g:else>
		<ul>
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<li class="connection ${c == connectionInstance ? 'selected' : ''}">
					<g:link action="show" id="${c.id}">
						<div class="connection-header">
							<h2>'${c.name}'</h2>
							<p class="connection-type">(<g:message code="${c.getClass().simpleName.toLowerCase()}.label"/>)</p>
							<i class="connection-status">${c.status}</i>
						</div>
					</g:link>
					
					<g:if test="${c == connectionInstance}">
						<g:if test="${c.status == RouteStatus.NOT_CONNECTED}">
							<div id="createRoute">
								<g:link controller="connection" action="createRoute" class="btn route" id="${c.id}"><g:message code="connection.list.route.create" /></g:link>
							</div>
							<div>
								<g:remoteLink controller="connection" action="wizard" class="btn route" id="${c.id}" onSuccess="launchMediumWizard('Edit connection', data, 'Done');"><g:message code="connection.edit" /></g:remoteLink>
							</div>
						</g:if>
						<g:else>
							<div>
								<g:remoteLink controller="connection" action="createTest" class="btn test" id="${c.id}" onSuccess="launchSmallPopup('Test message', data, 'Send');">
									<g:message code="connection.send.test.message" />
								</g:remoteLink>
								<g:link controller="connection" action="destroyRoute" class="btn" id="${c.id}">
									<g:message code="connection.route.destroy" />
								</g:link>
							</div>
						</g:else>
					</g:if>
				</li>
			</g:each>
		</ul>
	</g:else>
	<div id="create-connection-btn">
		<g:remoteLink class="btn" controller='connection' action="wizard" onSuccess="launchMediumWizard('New connection', data, 'Create');">
			<g:message code="connection.add" />
		</g:remoteLink>
	</div>
</div>
