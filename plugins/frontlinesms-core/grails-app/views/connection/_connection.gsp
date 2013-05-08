<tr class="connection-header connection" id="connection-${c?.id}">
	<td class="connection-status ${c.status}"></td>
	<td class="connection-type"><g:message code="${c.shortName}.label"/></td>
	<td class="connection-name">
		<h2>'${c.name}'</h2>
			<g:if test="${c instanceof frontlinesms2.SmssyncFconnection}"><%/* FIXME should not have SMSSync-specific code here.  Perhaps this should be here for all API implementers? */%>
				<p class="smssync-url">${serverUrl + createLink(uri: '/') + "api/1/smssync/" + c.id + "/"}</p>
			</g:if>
	</td>
	<td><div class="controls"></div></td>
</tr>

