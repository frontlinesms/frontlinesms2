<%@ page contentType="text/html;charset=UTF-8" %>

<ul>
	<li class="contacts">
		<h3><g:message code="contact.header"/></h3>
		<ul class="submenu">
			<li class="${contactsSection ? '' : 'selected'}">
				<g:link action="show"><g:message code="contact.all.contacts"/></g:link>
			</li>
			<li class="create">
				<g:link class="create btn contact" controller="contact" action="createContact">
					<g:message code="contact.create"/>
				</g:link>
			</li>
		</ul>
	</li>
	<li class="groups">
		<h3><g:message code="contact.groups.header"/></h3>
		<ul class="submenu">
			<g:each in="${groupInstanceList}" var="g">
				<li class="${contactsSection instanceof frontlinesms2.Group && contactsSection.id==g.id ? 'selected' : ''}">
					<g:link controller="group" action="show" id="${g.id}">${g.name}</g:link>
				</li>
			</g:each>
			<li class="create">
				<g:remoteLink class="btn create" controller="group" action="create" onLoading="showThinking();" onSuccess="hideThinking(); launchSmallPopup(i18n('smallpopup.group.title'), data, i18n('action.create'))">
					<g:message code="contact.create.group"/>
				</g:remoteLink>
			</li>
		</ul>
	</li>
	<li class="smartgroups">
		<h3><g:message code="contact.smartgroup.header"/></h3>
		<ul class="subemnu">
			<g:each in="${smartGroupInstanceList}" var="g">
				<li class="${contactsSection instanceof frontlinesms2.SmartGroup && contactsSection.id==g.id ? 'selected' : ''}">
					<g:link controller="smartGroup" action="show" id="${g.id}" elementId="smartgroup-link-${g.id}">${g.name}</g:link>
				</li>
			</g:each>
			<li class="create">
				<g:remoteLink class="create btn" controller="smartGroup" action="create" onLoading="showThinking();" onSuccess="hideThinking(); launchMediumPopup(i18n('popup.smartgroup.create'), data, (i18n('action.create')), createSmartGroup)">
					<g:message code="contact.create.smartgroup"/>
				</g:remoteLink>
			</li>
		</ul>
	</li>
</ul>

