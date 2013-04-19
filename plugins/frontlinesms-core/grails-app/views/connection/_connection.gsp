<tr class="connection-header connection ${c == connectionInstance ? 'selected' : ''}" id="connection-${c?.id}">
	<td class="connection-status ${c.status}"></td>
	<td class="connection-type"><g:message code="${c.shortName}.label"/></td>
	<td class="connection-name">
		<h2>'${c.name}'</h2>
			<g:if test="${c instanceof frontlinesms2.SmssyncFconnection}">
				<p class="smssync-url">${"http://&lt;your-ip-address&gt;"+createLink(uri: '/')+"api/1/smssync/"+c.id+"/"}</p>
			</g:if>
	</td>
	<td class="controls-container"><fsms:render template="selected_connection" model="[c:c]"/></td>
</tr>