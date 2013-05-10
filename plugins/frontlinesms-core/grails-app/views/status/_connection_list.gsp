<%@ page import="frontlinesms2.Contact" %>
<div id="connection-status">
	<ul id="connection-header">
		<li id="connection-title"><g:message code="status.connection.title"/></li>
		<li id="manage-connection">
			<g:link action="list" controller="connection">
				<g:message code="status.connection.manage"/> &raquo
			</g:link>
		</li>
	</ul>
	<g:if test="${connectionInstanceTotal == 0}">
		<p class="no-content"><g:message code="status.connection.none"/></p>
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
