<div id='connections'>
	<g:if test="${fconnectionInstanceTotal==0}">
		<div id='connections'>You have no connections configured.</div>
	</g:if>
	<g:else>
		<ul>
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<li class="connection ${c == connectionInstance ? 'selected' : ''}">
					<g:link action="connections" id="${c.id}">
						<div class="connection-header">
							<h2>'${c.name}'</h2>
							<p class="connection-type">(${c.type()})</p>
							<i class="connection-status">${c.status}</i>
						</div>
					</g:link>
					
					<g:if test="${c == connectionInstance}">
						<g:if test="${c.status == 'Not connected'}">
							<div>
								<g:link controller='connection' action="createRoute" class="btn route" id="${c.id}" >Create route</g:link>
							</div>
						</g:if>
						<g:else>
							<div>
								<g:remoteLink controller='connection' action="createTest" class="btn test" id="${c.id}"  onSuccess="launchSmallPopup('Test message', data, 'Send');">
									Send test message
								</g:remoteLink>
							</div>
						</g:else>
					</g:if>
				</li>
			</g:each>
		</ul>
	</g:else>
	<div id="create-connection-btn">
		<g:remoteLink class="btn" controller='connection' action="create_new" onSuccess="launchMediumWizard('New connection', data, 'Create');">
			Add new connection
		</g:remoteLink>
	</div>
</div>