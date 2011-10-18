<%@ page contentType="text/html;charset=UTF-8" %>
<ol class="context-menu" id="contacts-menu">
	<li class="section">
		<img src='${resource(dir:'images/icons',file:'contacts.png')}' />
		<h2>Contacts</h2>
		<ol class='sub-menu' id="contacts-submenu">
			<li class="${contactsSection ? '' : 'selected'}">
				<g:link action="show">All contacts</g:link>
			</li>
			<li class='create' id="create-contact">
				<g:link class="create contact" controller="contact" action="createContact" >
					Create new contact
				</g:link>
			</li>
		</ol>
	</li>
	<li class="section">
		<img src='${resource(dir:'images/icons',file:'groups.png')}' />
		<h2>Groups</h2>
		<ol class='sub-menu' id="groups-submenu">
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
		</ol>
	</li>
	<li class="section">
		<h2>Smart Groups</h2>
		<ol class="sub-menu" id="smart-groups-submenu">
			<g:if test="${smartGroupInstanceList.size() > 0}">
				<g:each in="${smartGroupInstanceList}" var="g">
					<li>
						<g:link controller="smartGroup" action="show" id="${g.id}">${g.name}</g:link>
					</li>
				</g:each>
			</g:if>		
			<g:else>
				<li id="no-smart-groups">No smart groups.</p>
			</g:else>
			<li class='create' id="create-smart-group">
				<g:remoteLink controller="smartGroup" action="create" onSuccess="launchMediumPopup('Create smart group', data, 'Create', function() { initSmartGroupWizard(); });">
					Create new smart group
				</g:remoteLink>
			</li>
		</ol>
	</li>
</ol>
