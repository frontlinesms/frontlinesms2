<g:if test="${fconnectionInstanceTotal==0}">
	<div id='connections'>You have no connections configured.</div>
</g:if>
<g:else>
	<ol id='connections'>
		<g:each in="${connectionInstanceList}" status="i" var="c">
			<li id="connection-${c.id}" style="display: inline">
				<div class="name">
					<g:if test="${c.status == 'Connected'}">
						<img src="../images/icons/status_green.gif"/>
					</g:if>
					<g:else>
						<img src="../images/icons/status_orange.gif"/>
					</g:else>
					${c.name}
				</div>
				<div class="value">             
					<div>${c.status}</div>
					<div>Signal Strength</div>
					<div>Balance</div>
				</div>
			</li>
		</g:each>
	</ol>
</g:else>
