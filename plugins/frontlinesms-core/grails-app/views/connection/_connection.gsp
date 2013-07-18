<%@ page import="frontlinesms2.api.FrontlineApi" %>
<tr class="connection-header connection" id="connection-${c?.id}">
	<td class="connection-status ${c.status}"></td>
	<td class="connection-type"><g:message code="${c.shortName}.label"/></td>
	<td class="connection-name">
		<h2>'${c.name}'</h2>
			<g:if test="${FrontlineApi.isAssignableFrom(c.class)}">
				<p class="api-url">${serverUrl + createLink(uri: '/') + c.fullApiUrl}</p>
			</g:if>
	</td>
	<td><div class="controls"></div></td>
</tr>

