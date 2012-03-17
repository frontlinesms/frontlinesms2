<div id='connections'>
	<g:if test="${fconnectionInstanceTotal==0}">
		<div>You have no connections configured.</div>
	</g:if>
	<g:else>
		<ul>
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<li class="connection ${c == connectionInstance ? 'selected' : ''}">
					<g:link action="connections" id="${c.id}">
						<div class="connection-header">
							<h2>'${c.name}'</h2>
							<p class="connection-type">(${c.type})</p>
							<i class="connection-status">${c.status}</i>
						</div>
					</g:link>
					
					<g:if test="${c == connectionInstance}">
						<g:if test="${c.status == 'Not connected'}">
							<div id="createRoute">
								<g:link controller="connection" action="createRoute" class="btn route" id="${c.id}">Create route</g:link>
							</div>
							<div>
								<g:remoteLink controller="connection" action="connection_wizard" class="btn route" id="${c.id}" onSuccess="launchMediumWizard('Edit connection', data, 'Done');">Edit Connection</g:remoteLink>
							</div>
						</g:if>
						<g:else>
							<div>
								<g:remoteLink controller="connection" action="createTest" class="btn test" id="${c.id}" onSuccess="launchSmallPopup('Test message', data, 'Send');">
									Send test message
								</g:remoteLink>
								<g:link controller="connection" action="destroyRoute" class="btn" id="${c.id}">
									Destroy route
								</g:link>
							</div>
						</g:else>
					</g:if>
				</li>
			</g:each>
		</ul>
	</g:else>
	<div id="create-connection-btn">
		<g:remoteLink class="btn" controller='connection' action="connection_wizard" onSuccess="launchMediumWizard('New connection', data, 'Create');">
			Add new connection
		</g:remoteLink>
	</div>
</div>
