<%@ page contentType="text/html;charset=UTF-8" %>
<fsms:menu class="contacts">
	<fsms:submenu code="contact.header" class="contacts">
		<fsms:menuitem selected="${!contactsSection}" controller="contact" action="show" code="contact.all.contacts" msgargs="${[contactInstanceTotal]}"/>
		<fsms:menuitem bodyOnly="true" class="create">
			<g:link class="create btn contact" controller="contact" action="createContact">
				<g:message code="contact.create"/>
			</g:link>
		</fsms:menuitem>
		<fsms:menuitem bodyOnly="true" class="import">
			<fsms:popup class="btn" controller="import" action="contactWizard" popupCall="mediumPopup.launchContactImportPopup(i18n('contact.import.label'), data)"><g:message code="contact.import.label"/></fsms:popup>
		</fsms:menuitem>
		<fsms:menuitem bodyOnly="true" class="">
			<g:if test="${contactsSection?.shortName}">
				<fsms:popup class="btn" controller="export" id="export" action="contactWizard" params="[groupId:contactsSection?.id, contactsSection:contactsSection?.shortName]" popupCall="launchSmallPopup(i18n('smallpopup.contact.export.title'), data, i18n('action.export'))"><g:message code="group.export.label" args="${[contactsSection?.name]}"/></fsms:popup>
			</g:if>
			<g:else>
				<fsms:popup class="btn" controller="export" id="export" action="contactWizard" popupCall="launchSmallPopup(i18n('smallpopup.contact.export.title'), data, i18n('action.export'))"><g:message code="contact.export.label"/></fsms:popup>
			</g:else>
		</fsms:menuitem>
	</fsms:submenu>

	<fsms:submenu code="contact.groups.header" class="groups">
		<g:each in="${groupInstanceList}" var="g">
			<fsms:menuitem selected="${contactsSection instanceof frontlinesms2.Group && contactsSection.id==g.id}" controller="group" action="show" string="${g.name}" id="${g.id}" />
		</g:each>
		<fsms:menuitem bodyOnly="true" class="create">
			<fsms:popup class="btn create" controller="group" action="create" popupCall="launchSmallPopup(i18n('smallpopup.group.create.title'), data, i18n('action.create'))">
				<g:message code="contact.create.group"/>
			</fsms:popup >
		</fsms:menuitem>
	</fsms:submenu>
	<fsms:submenu code="contact.smartgroup.header" class="smartgroups">
		<g:each in="${smartGroupInstanceList}" var="g">
			<fsms:menuitem selected="${contactsSection instanceof frontlinesms2.SmartGroup && contactsSection.id==g.id}" controller="smartGroup" action="show" string="${g.name}" id="${g.id}" />
		</g:each>
		<fsms:menuitem bodyOnly="true" class="create">
			<fsms:popup  class="create btn" controller="smartGroup" action="create" popupCall="mediumPopup.launchMediumPopup(i18n('popup.smartgroup.create'), data, (i18n('action.create')), createSmartGroup)">
				<g:message code="contact.create.smartgroup"/>
			</fsms:popup >
		</fsms:menuitem>
	</fsms:submenu>
</fsms:menu>

<r:script>
	var createSmartGroup = function() {
		$("#submit").attr('disabled', 'disabled');
		if(validateSmartGroup()) {
			$(this).find("form").submit();
			$(this).dialog('close');
		} else {
			$("#submit").removeAttr('disabled');
			$('.error-panel').show();
		}
	};
</r:script>
