<%@ page contentType="text/html;charset=UTF-8" %>
  	<ul class="content-menu" id="contacts-menu">
		<li>
			<h2>Create new...</h2>
			<ol id="create-submenu">
				<li id="create-contact">
					<g:link class="create contact" action="createContact">Contact</g:link>
				</li>
				<li id="create-group">
					<g:link class="create group" action="createGroup">Group</g:link>
				</li>
				<li id="manage-subscription">
					<g:remoteLink controller="group" action="list" onSuccess="launchWizard('manage_subscription', data);">
						Manage Subscription
					</g:remoteLink>
				</li>
			</ol>
		</li>
		<li>
			<h2>Contacts</h2>
			<ol id="contacts-submenu">
				<li>
					<g:link class="${contactsSection ? '' : 'selected'}" action="list">All contacts</g:link>
				</li>
			</ol>
			<h2>Groups</h2>
			<ol id="groups-submenu">
				<g:each in="${groupInstanceList}" status="i" var="g">
					<li>
						<g:link controller="group" action="show" id="${g.id}" class="${contactsSection instanceof frontlinesms2.Group && contactsSection.id==g.id ? 'selected' : ''}">${g.name}</g:link>
					</li>
				</g:each>
			</ol>
		</li>
  	</ul>
