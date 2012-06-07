<%@ page import="frontlinesms2.Contact" %>
<div id="connection-status">
	<h3 id="connection-title"><g:message code="status.connection.header"/></h3>
	<g:if test="${connectionInstanceTotal == 0}">
		<div id='connections'><g:message code="status.connection.none"/></div>
	</g:if>
	<g:else>
		<table id='stored-connections'>
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<tr id="connection-${c.id}">
					<td class="name ${c.status}">
						${c.name}
					</td>
					<td class="value">             
						<g:message code="${c.status.i18n}"/>
					</td>
				</tr>
			</g:each>
		</table>
	</g:else>
</div>
