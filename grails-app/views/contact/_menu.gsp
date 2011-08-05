<%@ page contentType="text/html;charset=UTF-8" %>
<ol class="context-menu" id="contacts-menu">
	<li class="section">
		<h2>Contacts</h2>
		<ol class='sub-menu' id="contacts-submenu">
			<li>
				<g:link class="${contactsSection ? '' : 'selected'}" action="list">All contacts</g:link>
			</li>
			<li class='create' id="create-contact">
				<g:link class="create contact" action="createContact">
					Create new contact
				</g:link>
			</li>
		</ol>
	</li>
	<li class="section">
		<h2>Groups</h2>
		<ol class='sub-menu' id="groups-submenu">
			<g:each in="${groupInstanceList}" status="i" var="g">
				<li>
					<g:link controller="group" action="show" id="${g.id}" class="${contactsSection instanceof frontlinesms2.Group && contactsSection.id==g.id ? 'selected' : ''}">${g.name}</g:link>
				</li>
			</g:each>
			<li class='create' id="create-group">
				<g:remoteLink action="createGroup" onSuccess="launchSmallPopup('Group', data, 'Create');">
					Create new group
				</g:remoteLink>
			</li>
		</ol>
	</li>
</ol>
