<%@ page contentType="text/html;charset=UTF-8" %>
<fsms:menu class="contacts">
	<fsms:submenu code="contact.header" class="contacts">
		<fsms:menuitem selected="${!contactsSection}" controller="contact" action="show" code="contact.all.contacts" />
		<fsms:menuitem bodyOnly="true" class="create">
			<g:link class="create btn contact" controller="contact" action="createContact">
				<g:message code="contact.create"/>
			</g:link>
		</fsms:menuitem>
	</fsms:submenu>

	<fsms:submenu code="contact.groups.header" class="groups">
		<g:each in="${groupInstanceList}" var="g">
			<fsms:menuitem selected="${contactsSection instanceof frontlinesms2.Group && contactsSection.id==g.id}" controller="group" action="show" string="${g.name}" id="${g.id}" />
		</g:each>
		<fsms:menuitem bodyOnly="true" class="create">
			<g:remoteLink class="btn create" controller="group" action="create" onLoading="showThinking();" onSuccess="hideThinking(); launchSmallPopup(i18n('smallpopup.group.title'), data, i18n('action.create'))">
				<g:message code="contact.create.group"/>
			</g:remoteLink>
		</fsms:menuitem>
	</fsms:submenu>
	<fsms:submenu code="contact.smartgroup.header" class="smartgroups">
		<g:each in="${smartGroupInstanceList}" var="g">
			<fsms:menuitem selected="${contactsSection instanceof frontlinesms2.SmartGroup && contactsSection.id==g.id}" controller="smartGroup" action="show" string="${g.name}" id="${g.id}" />
		</g:each>
		<fsms:menuitem bodyOnly="true" class="create">
			<g:remoteLink class="create btn" controller="smartGroup" action="create" onLoading="showThinking();" onSuccess="hideThinking(); launchMediumPopup(i18n('popup.smartgroup.create'), data, (i18n('action.create')), createSmartGroup)">
				<g:message code="contact.create.smartgroup"/>
			</g:remoteLink>
		</fsms:menuitem>
	</fsms:submenu>
</fsms:menu>
