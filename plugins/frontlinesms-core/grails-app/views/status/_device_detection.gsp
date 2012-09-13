<div id="device-detection">
	<h3 id="detection-title"><g:message code="status.devises.header"/></h3>
	<g:link class="btn" action="detectDevices"><g:message code="status.detect.modems"/></g:link>
	<table id="detected-devices">
		<thead>
			<tr>
				<th><g:message code="modem.port"/></th>
				<th class="description"><g:message code="modem.description"/></th>
				<th><g:message code="modem.locked"/></th>
			</tr>
		</thead>
		<tbody>
			<g:if test="${detectedDevices.size() == 0}">
				<tr><td colspan="3" class="no-content"><g:message code="status.modems.none"/></td></tr>
			</g:if>
			<g:else>
				<g:each in="${detectedDevices}" var="d">
					<tr>
						<td>${d.port}</td>
						<td>${d.description}</td>
						<td>${d.lockType}</td>
					</tr>
				</g:each>
			</g:else>
		</tbody>
	</table>
</div>
