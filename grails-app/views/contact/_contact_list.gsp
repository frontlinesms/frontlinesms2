<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${contactsSection instanceof frontlinesms2.Group}">
	<h2>${contactsSection.name}</h2>
</g:if>
<g:elseif test="${!contactInstance}">
	<h2>New Group</h2>
</g:elseif>
<g:else>
	<h2>${contactInstance.name?:contactInstance.primaryMobile?:'New Contact'}</h2>
</g:else>
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
