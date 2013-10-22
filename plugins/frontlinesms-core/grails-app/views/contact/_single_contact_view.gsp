<div id="single-contact" class="single-contact ${(contactInstance?.id)?'edit':''}">
	<g:if test="${contactInstance}">
		<g:hiddenField name="contactId" value="${contactInstance?.id}"/>
	</g:if>
	<g:hiddenField name="groupsToAdd" value=","/>
	<g:hiddenField name="groupsToRemove" value=","/>
	<g:hiddenField name="fieldsToAdd" value=","/>
	<g:hiddenField name="fieldsToRemove" value=","/>
	<table>
		<tr class="editable">
			<td><label for="name"><g:message code="contact.name.label"/></label></td>
			<td>
				<g:textField name="name" value="${contactInstance?.name}" title='Click to edit'/>
			</td>
		</tr>
		<tr class="editable">
			<td><label for="mobile"><g:message code="contact.mobile.label"/></label></td>
			<td>
				<g:textField class="phoneNumber" name="mobile" value="${contactInstance?.mobile?.trim()}" onchange="validateMobile(this)" title='Click to edit'/>
				<span><i class="icon-edit"></i></span>
				<p class="warning" style="display:none"><g:message code="contact.phonenumber.international.warning"/></p>
			</td>
		</tr>
		<tr class="editable">
			<td><label for="email"><g:message code="contact.email.label"/></label></td>
			<td>
				<g:textField name="email" class="email" value="${contactInstance?.email?.trim()}" title='Click to edit'/>
				<span><i class="icon-edit"></i></span>
			</td>
		</tr>
		<g:each in="${contactFieldInstanceList}" status="i" var="f">
			<tr class="input editable ${f==fieldInstance? 'selected': ''}">
				<td><label for="custom-field-${f.name}">${f.name}</label></td>
				<td>
					<input type="text" name="${f.name}" id="field-item-${f.name}" value="${f.value}" title='Click to edit'/>
					<a id="remove-field-${f.id}" class="icon-remove custom-field remove-command"></a>
				</td>
			</tr>
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
		<tr id="note-area" class="input basic-info editable">
			<td><label for="notes"><g:message code="contact.notes.label"/></label></td>
			<td><g:textArea name="notes" id="notes" value="${contactInstance?.notes}" title='Click to edit'/></td>
		</tr>
		<tr id="group-section" class="input basic-info">
			<td><label for="groups"><g:message code="contact.groups.label"/></label></td>
			<td>
				<ul id="group-list">
					<g:each in="${contactGroupInstanceList}" status="i" var="g">
						<li class="${g == groupInstance ? 'selected' : ''}" groupName="${g.name}">
							<span>${g.name}</span>
							<a class="remove-command icon-remove" id="remove-group-${g.id}"></a>
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
		<g:if test="${contactInstance?.id}">
			<g:actionSubmit class="btn save" id="update-single" action="update" value="${g.message(code:'action.save')}" disabled="disabled"/>
			<g:link class="cancel btn disabled"><g:message code="action.cancel"/></g:link>
		</g:if>
		<g:else>
			<g:actionSubmit class="btn" action="saveContact" value="${g.message(code:'action.save')}"/>
			<g:link class="cancel btn" action="index"><g:message code="action.cancel"/></g:link>
		</g:else>
		
		<g:if test="${contactInstance?.id}">
			<i class="icon-remove-sign"></i>
			<g:link elementId="btn_delete" url="#" onclick="launchConfirmationPopup(i18n('smallpopup.contact.delete.title'));" class="btn-delete">
				<g:message code="contact.action.delete"/>
			</g:link>
		</g:if>

		<g:if test="${contactInstance?.mobile?.trim()}">
			<fsms:popup class="icon-envelope send-message" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.mobile]" popupCall="mediumPopup.launchMediumWizard(i18n('wizard.send.message.title'), data, i18n('wizard.send'), true);">
			<g:message code="message.content.asd"/>
			</fsms:popup>
		</g:if>
	</div>
	<g:if test="${contactInstance && contactInstance.id}">
		<div id="message-stats">
			<h2><g:message code="contact.messages.label"/></h2>
			<ul>
				<li class="sent"><g:message code="contact.messages.sent" args="${[contactInstance?.outboundMessagesCount]}"/></li>
				<li class="received"><g:message code="contact.received.messages" args="${[contactInstance?.inboundMessagesCount]}"/></li>
			</ul>
			<g:link class="btn search" controller='search' action='result'
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

	$("td > input[type=text]").each(function(index) {
		var clear = $(this).next();
		if($(this).val() === "") {
			clear.addClass("hidden");
		} else {
			clear.removeClass("hidden");
		}
	}).keyup(function() {
		var clear = $(this).next();
		if($(this).val() !== "") {
			clear.removeClass("hidden");
			if($(this).attr("name") === "mobile") {
				$(".send-message").removeClass("hidden");
			}
		} else {
			clear.addClass("hidden");
			if($(this).attr("name") === "mobile") {
				$(".send-message").addClass("hidden");
			}
		}
	});

	contactCtrl.init();
});

var contactCtrl = function() {
	var formHash = 0,
	contactEditForm = $(".contact-edit-form"),
	contactFormDirtyCallBack = function() { contactEditForm.trigger("contactFormDirty"); },
	init = function() {
		formHash = contactEditForm.serialize().hashCode();
		$("#notes").autosize();

		bindOnFormDataChangedListeners();

		contactEditForm.on("addedCustomFieldToContact", function() {
			$(".edit input[type=text]").on("blur", contactFormDirtyCallBack);
			$(".edit .remove-command").on("click", contactFormDirtyCallBack);			
		});

		contactEditForm.on("addedGroupToContact", function() {
			$(".edit .remove-command").on("click", contactFormDirtyCallBack);
		});

		contactEditForm.on("contactFormDirty", function() {
			updateContactData();
		});
	},
	bindOnFormDataChangedListeners = function() {
		$(".edit input[type=text]").on("blur", contactFormDirtyCallBack);
		$(".edit input[type=hidden]").on("change", contactFormDirtyCallBack);
		$(".edit select").on("change", contactFormDirtyCallBack);
		$(".edit textarea").on("blur", contactFormDirtyCallBack);
		$(".edit .remove-command").on("click", contactFormDirtyCallBack);
	},
	updateContactData = function() {
		if(formDataChanged) {
			$.ajax({
				type : 'POST',
				url : url_root + "contact/saveContact",
				data : contactEditForm.serialize(),
				success : function(data) {
					console.log(data);
				}
			});
		}
	},
	formDataChanged = function() {
		return (contactEditForm.serialize().hashCode() != formHash);
	};

	return {
		init : init
	}
}();
</r:script>

