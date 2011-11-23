<%@ page import="frontlinesms2.Contact" %>
<h3 id="connection-title">Connections</h3>
<div id="connection-status">
	<g:if test="${connectionInstanceTotal == 0}">
		<div id='connections'>You have no connections configured.</div>
	</g:if>
	<g:else>
		<table id='connections'>
			<g:each in="${connectionInstanceList}" status="i" var="c">
				<tr id="connection-${c.id}">
					<td class="name ${c.status}">
						${c.name}
					</td>
					<td class="value">             
						<div>${c.status}</div>
					</td>
				</tr>
			</g:each>
		</table>
	</g:else>
</div>