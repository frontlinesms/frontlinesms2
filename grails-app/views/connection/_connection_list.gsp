	<g:if test="${fconnectionInstanceTotal==0}">
		<div id='connections'>You have no connections configured.</div>
	</g:if>
	<g:else>
		<ol id='connections'>
			<g:each in="${connectionInstanceList}" status="i" var="c">
					<li class="connection ${c == connectionInstance ? 'selected' : ''}">
						<ol>
							<li class='con-name' >
								<g:link action="connections" id="${c.id}">'${c.name}'</g:link>
							</li>
							<li class='con-type'>
								<g:link action="connections" id="${c.id}">(${c.type()})</g:link>
							</li>
							<li class="con-status">${c.status}</li>
						</ol><br />
						<g:if test="${c == connectionInstance}">
							<g:if test="${c.status == 'Not connected'}">
								<div class="buttons">
									<g:link controller='connection' action="createRoute" class="route" id="${c.id}" >Create route</g:link>
								</div>
							</g:if>
							<g:else>
								<div class="buttons">
									<g:remoteLink controller='connection' action="createTest" class="test" id="${c.id}"  onSuccess="launchSmallPopup('Test message', data, 'Send');">
										Send test message
									</g:remoteLink>
								</div>
							</g:else>
						</g:if>
					</li>
			</g:each>
		</ol>
	</g:else>
	<div id='btnNewConnection'>
		<g:remoteLink controller='connection' action="create_new" onSuccess="launchMediumWizard('New connection', data, 'Create');">
			Add new connection
		</g:remoteLink>
	</div>
