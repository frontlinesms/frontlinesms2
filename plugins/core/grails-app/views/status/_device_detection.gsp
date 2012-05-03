<div id="device-detection">
	<g:link class="btn" action="detectDevices"><g:message code="status.detect.modems"/></g:link>
	<g:if test="${detectedDevices.size() == 0}">
		<p><g:message code="status.modems.none"/></p>
	</g:if>
	<g:else>
		<table id="detected-devices">
			<thead>
				<tr>
					<td><g:message code="modem.port"/></td>
					<td><g:message code="modem.description"/></td>
					<td><g:message code="modem.locked"/></td>
				</tr>
			</thead>
			<tbody>
				<g:each in="${detectedDevices}" var="d">
					<tr>
						<td>${d.port}</td>
						<td>${d.description}</td>
						<td>${d.lockType}</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:else>
</div>
