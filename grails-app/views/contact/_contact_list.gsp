<%@ page contentType="text/html;charset=UTF-8" %>

<g:if test="${contactInstanceTotal > 0}">
	<ol id="contacts">
		<g:each in="${contactInstanceList}" status="i" var="c">
			<li class="${c == contactInstance ? 'selected' : ''}">
				<g:if test="${contactsSection instanceof frontlinesms2.Group}">
					<g:link controller="contact" action="show" params="[contactId:c.id, groupId:contactsSection.id]">${c.name?:c.primaryMobile?:'[No Name]'}</g:link>
				</g:if>
				<g:else>
					<g:link action="show" id="${c.id}">${c.name?:c.primaryMobile?:'[No Name]'}</g:link>
				</g:else>
			</li>
		</g:each>
	</ol>
</g:if>
<g:else>
	<div id="contacts">
		You have no contacts saved
	</div>
</g:else>
