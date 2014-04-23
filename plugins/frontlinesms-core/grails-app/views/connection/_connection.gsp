<%@ page import="frontlinesms2.api.FrontlineApi" %>
<tr class="connection-header connection ${c.sendEnabled ? '' : 'receiveOnly'} ${c.status?.toString()?.toLowerCase()} inline-editable-parent" id="connection-${c?.id}">
	<td class="connection-status ${c.status}"></td>
	<td class="connection-type"><g:message code="${c.shortName}.label"/></td>
	<td class="connection-flag"><i class="${c.flagCSSClasses}"></i></td>
	<td class="connection-name">
		<h2><fsms:inlineEditable instance="${c}" field="name" /></h2>
			<fsms:templateElseBody template="/connection/${c.shortName}">
				<g:if test="${FrontlineApi.isAssignableFrom(c.class)}">
					<p class="api-url">${c.getFullApiUrl(request)}</p>
				</g:if>
				<g:elseif test="${c.displayMetadata}">
					<p>${c.displayMetadata}</p>
				</g:elseif>
			</fsms:templateElseBody>
	</td>
	<td><div class="controls"></div></td>
</tr>

