<%@ page contentType="text/html;charset=UTF-8" %>
<ul class="main-list" id="contacts-menu">
	<li>
		<h3 class="list-title">Contacts</h3>
		<ul class='sublist' id="contacts-submenu">
			<li class="${contactsSection ? '' : 'selected'}">
				<g:link action="show">All contacts</g:link>
			</li>
			<li class='create' id="create-contact">
				<g:link class="create contact" controller="contact" action="createContact" >
					Create new contact
				</g:link>
			</li>
		</ul>
	</li>
	<li>
		<h3 class="list-title">Groups</h3>
		<ul class='sublist' id="groups-submenu">
			<g:each in="${groupInstanceList}" var="g">
				<li class="${contactsSection instanceof frontlinesms2.Group && contactsSection.id==g.id ? 'selected' : ''}">
					<g:link controller="group" action="show" id="${g.id}">${g.name}</g:link>
				</li>
			</g:each>
			<li class='create' id="create-group">
				<g:remoteLink controller="group" action="create" onSuccess="launchSmallPopup('Group', data, 'Create');">
					Create new group
				</g:remoteLink>
			</li>
		</ul>
	</li>
	<li class="section">
		<h3 class="list-title">Smart Groups</h3>
		<ul class="sublist" id="smart-groups-submenu">
			<g:if test="${smartGroupInstanceList.size() > 0}">
				<g:each in="${smartGroupInstanceList}" var="g">
					<li class="${contactsSection instanceof frontlinesms2.SmartGroup && contactsSection.id==g.id ? 'selected' : ''}">
						<g:link controller="smartGroup" action="show" id="${g.id}" elementId="smartgroup-link-${g.id}">${g.name}</g:link>
					</li>
				</g:each>
			</g:if>		
			<g:else>
				<li id="no-smart-groups">No smart groups.</p>
			</g:else>
			<li class='create' id="create-smart-group">
				<g:remoteLink controller="smartGroup" action="create" onSuccess="launchMediumPopup('Create smart group', data, 'Create', createSmartGroup);">
					Create new smart group
				</g:remoteLink>
			</li>
		</ul>
	</li>
</ul>
