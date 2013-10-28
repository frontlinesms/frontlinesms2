<div id="single-contact" class="single-contact ${(contactInstance?.id)?'edit':''}">
	<g:if test="${contactInstance}">
		<g:hiddenField name="contactId" value="${contactInstance?.id}"/>
	</g:if>
	<g:hiddenField name="groupsToAdd" value=","/>
	<g:hiddenField name="groupsToRemove" value=","/>
	<g:hiddenField name="fieldsToAdd" value=","/>
	<g:hiddenField name="fieldsToRemove" value=","/>
	<table>
		<tr class="editable" title="${g.message(code:'contact.field.click.to.edit')}">
			<td><label for="name"><g:message code="contact.name.label"/></label></td>
			<td>
				<g:textField name="name" value="${contactInstance?.name}" placeholder="${g.message(code:'contact.field.name.placeholder')}"/>
				<span><i class="icon-edit"></i></span>
			</td>
		</tr>
		<tr class="editable" title="${g.message(code:'contact.field.click.to.edit')}">
			<td><label for="mobile"><g:message code="contact.mobile.label"/></label></td>
			<td>
				<g:textField class="phoneNumber" name="mobile" value="${contactInstance?.mobile?.trim()}" onchange="validateMobile(this)" placeholder="${g.message(code:'contact.field.mobile.placeholder')}"/>
				<span><i class="icon-edit"></i></span>
				<p class="warning" style="display:none"><g:message code="contact.phonenumber.international.warning"/></p>
			</td>
		</tr>
		<tr class="editable" title="${g.message(code:'contact.field.click.to.edit')}">
			<td><label for="email"><g:message code="contact.email.label"/></label></td>
			<td>
				<g:textField name="email" class="email" value="${contactInstance?.email?.trim()}" placeholder="${g.message(code:'contact.field.email.placeholder')}"/>
				<span><i class="icon-edit"></i></span>
			</td>
		</tr>
		<g:each in="${contactFieldInstanceList}" status="i" var="f">
			<fsms:render template="/contact/custom_field" model='[fieldName:"customField-${f.id}", removerName:"remove-field-${f.id}", name:f.name, value:f.value, selected:f==fieldInstance]'/>
		</g:each>
		<tr>
			<td></td>
			<td id="info-add" class="button-container">
				<select class="dropdown" id="new-field-dropdown" name="new-field-dropdown">
					<option class="not-field" value="na">
						<g:message code="contact.customfield.addmoreinformation"/>
					</option>
					<g:each in="${uniqueFieldInstanceList}" status="i" var="f">
						<option value="${f}">${f}</option>
					</g:each>
					<option class="create-custom-field" value='add-new'>
						<g:message code="contact.customfield.option.createnew"/>
					</option>
				</select>
			</td>
			<td></td>
		</tr>
		<tr id="note-area" class="input basic-info editable" title="${g.message(code:'contact.field.click.to.edit')}">
			<td><label for="notes"><g:message code="contact.notes.label"/></label></td>
			<td>
				<g:textArea name="notes" id="notes" value="${contactInstance?.notes}"/>
				<span><i class="icon-edit"></i></span>
			</td>
		</tr>
		<tr id="group-section" class="input basic-info">
			<td><label for="groups"><g:message code="contact.groups.label"/></label></td>
			<td>
				<ul id="group-list">
					<g:each in="${contactGroupInstanceList}" status="i" var="g">
						<li class="group ${g == groupInstance ? 'selected' : ''}" groupName="${g.name}">
							<span>${g.name}</span>
							<a class="icon-remove remove-command" id="remove-group-${g.id}"></a>
						</li>
					</g:each>
					<li id="no-groups" style="${contactGroupInstanceList?'display: none':''}">
						<p><g:message code="contact.notinanygroup.label"/></p>
					</li>
				</ul>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<select id="group-dropdown" name="group-dropdown" class="dropdown" onchange="selectmenuTools.snapback(this)">
					<option class="not-group"><g:message code="contact.add.to.group"/></option>
					<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
						<option value="${g.id}">${g.name}</option>
					</g:each>
				</select>
			</td>
		</tr>
	</table>
	<div id="action-buttons" class="buttons">
		<g:if test="${contactInstance?.mobile?.trim()}">
			<fsms:popup class="icon-envelope send-message stroked block btn" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.mobile]" popupCall="mediumPopup.launchMediumWizard(i18n('wizard.send.message.title'), data, i18n('wizard.send'), true);">
			<g:message code="contact.send.message" args="${[contactInstance.name?:contactInstance.mobile]}"/>
			</fsms:popup>
		</g:if>
		<g:if test="${contactInstance?.id}">
			<g:link elementId="btn_delete" url="#" onclick="launchConfirmationPopup(i18n('smallpopup.contact.delete.title'));" class="btn-delete btn stroked warn">
				<i class="icon-remove-sign"></i>
				<span><g:message code="contact.action.delete"/></span>
			</g:link>
		</g:if>
		<g:if test="${contactInstance?.id}">
			<g:actionSubmit class="btn stroked save" id="update-single" action="update" value="${g.message(code:'action.save')}" disabled="disabled"/>
			<g:link class="cancel btn stroked disabled"><g:message code="action.cancel"/></g:link>
		</g:if>
		<g:else>
			<g:actionSubmit class="btn stroked" action="saveContact" value="${g.message(code:'action.save')}"/>
			<g:link class="cancel stroked btn warn" action="index"><g:message code="action.cancel"/></g:link>
		</g:else>
	</div>
	<g:if test="${contactInstance && contactInstance.id}">
		<div id="message-stats">
			<h2><g:message code="contact.messages.label"/></h2>
			<ul>
				<li class="sent"><g:message code="contact.messages.sent" args="${[contactInstance?.outboundMessagesCount]}"/></li>
				<li class="received"><g:message code="contact.received.messages" args="${[contactInstance?.inboundMessagesCount]}"/></li>
			</ul>
			<g:link class="btn stroked icon-search search" controller='search' action='result'
					params="${contactInstance?.name? [contactString: contactInstance?.name]:[searchString: contactInstance?.mobile]}">
				<g:message code="contact.search.messages"/>
			</g:link>
		</div>
	</g:if>
</div>
<r:script>
$(function() {
	app_info.listen("contact_message_stats", { id: "${contactInstance?.id}" }, function(data) {
		data = data.contact_message_stats;
		if(!data) { return; }
		$("#message-stats .sent").text(i18n("contact.messages.sent", data.outbound));
		$("#message-stats .recieved").text(i18n("contact.messages.received", data.inbound));
	});
});
</r:script>

<fsms:render template="/contact/custom_field" type="sanchez" id="custom-field-input" runtimeVars="name,fieldName,removerName" model="[selected:false, value:'']"/>

