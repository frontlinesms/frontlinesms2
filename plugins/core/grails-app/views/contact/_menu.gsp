<%@ page contentType="text/html;charset=UTF-8" %>
<div id="sidebar">
	<ul class="main-list" id="contacts-menu">
		<li>
			<h3 id="contacts-list-title" class="list-title">Contacts</h3>
			<ul class='sublist' id="contacts-submenu">
				<li class="${contactsSection ? '' : 'selected'}">
					<g:link action="show">All contacts</g:link>
				</li>
				<li class='create' id="create-contact">
					<g:link class="create btn contact" controller="contact" action="createContact" >
						Create new contact
					</g:link>
				</li>
			</ul>
		</li>
		<li>
			<h3 id="groups-list-title" class="list-title">Groups</h3>
			<ul class='sublist' id="groups-submenu">
				<g:each in="${groupInstanceList}" var="g">
					<li class="${contactsSection instanceof frontlinesms2.Group && contactsSection.id==g.id ? 'selected' : ''}">
						<g:link controller="group" action="show" id="${g.id}">${g.name}</g:link>
					</li>
				</g:each>
				<li class='create' id="create-group">
					<g:remoteLink class="btn create" controller="group" action="create" onSuccess="launchSmallPopup('Group', data, 'Create');">
						Create new group
					</g:remoteLink>
				</li>
			</ul>
		</li>
		<li class="section">
			<h3 id="smart-groups-list-title" class="list-title">Smart Groups</h3>
			<ul class="sublist" id="smart-groups-submenu">
				<g:each in="${smartGroupInstanceList}" var="g">
					<li class="${contactsSection instanceof frontlinesms2.SmartGroup && contactsSection.id==g.id ? 'selected' : ''}">
						<g:link controller="smartGroup" action="show" id="${g.id}" elementId="smartgroup-link-${g.id}">${g.name}</g:link>
					</li>
				</g:each>
				<li class='create' id="create-smart-group">
					<g:remoteLink class="create btn" controller="smartGroup" action="create" onSuccess="launchMediumPopup('Create smart group', data, 'Create', createSmartGroup);">
						Create new smart group
					</g:remoteLink>
				</li>
			</ul>
		</li>
	</ul>
</div>
