<%@ page import="frontlinesms2.RouteStatus" %>
<div id='connections'>
	<g:if test="${fconnectionInstanceTotal==0}">
		<div id="no-connection"><g:message code="connection.list.none"/></div>
	</g:if>
	<g:else>
		<ul>
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<li class="connection ${c == connectionInstance ? 'selected' : ''}">
					<g:link action="show" id="${c.id}">
						<div class="connection-header">
							<h2>'${c.name}'</h2>
							<p class="connection-type">(<g:message code="${c.getClass().simpleName.toLowerCase()}.label"/>)</p>
							<p class="connection-status"><g:message code="${c.status.i18n}"/></p>
						</div>
					</g:link>
					
					<g:if test="${c == connectionInstance}">
						<div class="controls">
							<g:if test="${c.status == RouteStatus.NOT_CONNECTED}">
								<g:link controller="connection" action="createRoute" class="btn route" id="${c.id}"><g:message code="connection.route.create"/></g:link>
								<g:remoteLink controller="connection" action="wizard" class="btn route" id="${c.id}" onSuccess="launchMediumWizard(i18n('connection.edit'), data, i18n('popup.done'), 675, 500, false)">
										<g:message code="connection.edit"/>
									</g:remoteLink>
								<g:link controller="connection" action="delete" class="btn route" id="${c.id}">
									<g:message code="connection.delete"/>
								</g:link>
							</g:if>
							<g:elseif test="${c.status == RouteStatus.CONNECTED}">
								<g:remoteLink controller="connection" action="createTest" class="btn test" id="${c.id}" onSuccess="launchSmallPopup(i18n('smallpopup.test.message.title'), data, i18n('smallpopup.send'))">
										<g:message code="connection.send.test.message"/>
								</g:remoteLink>
								<g:link controller="connection" action="destroyRoute" class="btn" id="${c.id}">
									<g:message code="connection.route.destroy"/>
								</g:link>
							</g:elseif>
						</div>
					</g:if>
				</li>
			</g:each>
		</ul>
	</g:else>
	<div class="controls">
		<g:remoteLink class="btn" controller='connection' action="wizard" onLoading="showThinking()" onSuccess="hideThinking(); launchMediumWizard(i18n('connection.add'), data, i18n('wizard.create'), 675, 500, false)">
			<g:message code="connection.add" />
		</g:remoteLink>
	</div>
</div>

