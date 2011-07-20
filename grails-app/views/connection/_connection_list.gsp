<g:if test="${fconnectionInstanceTotal==0}">
	<div id='connections'>You have no connections configured.</div>
</g:if>
<g:else>
	<ol id="connections">
		<g:each in="${connectionInstanceList}" status="i" var="c">
				<li class="${c == connectionInstance ? 'selected' : ''}">
					<g:link action="show" class="show" id="${c.id}">
						<h2>${c.name}</h2>
						<h3>${c.type()}</h3>
						<div class="status">${c.status}</div>
					</g:link>
					<g:if test="${c == connectionInstance}">
						<g:if test="${c.status == 'Not connected'}">
							<div class="buttons">
								<g:link action="createRoute" class="route" id="${c.id}" >Create route</g:link>
							</div>
						</g:if>
						<g:else>
							<g:link action="createTest" class="test" id="${c.id}" >Send test message</g:link>
						</g:else>
					</g:if>
				</li>
		</g:each>
	</ol>
</g:else>
<div id='btnNewConnection'>
	<g:link action='create'>Add new connection</g:link>
</div>