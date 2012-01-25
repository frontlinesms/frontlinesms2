<div id="device-detection">
	<g:link class="btn" action="detectDevices">Detect Modems</g:link>
	<g:if test="${detectedDevices.size() == 0}">
		<p>No devices have been detected yet.</p>
	</g:if>
	<g:else>
		<table id="detected-devices">
			<thead>
				<tr>
					<td>Port</td>
					<td>Description</td>
				</tr>
			</thead>
			<tbody>
				<g:each in="${detectedDevices}" var="d">
					<tr>
						<td>${d.port}</td>
						<td>${d.description}</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:else>
</div>
