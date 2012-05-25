<jqval:resources/>
<div id="single-contact" class="single-contact">
	<g:if test="${contactInstance}">
		<g:hiddenField name="contactId" value="${contactInstance?.id}"/>
	</g:if>
	<g:hiddenField name="groupsToAdd" value=","/>
	<g:hiddenField name="groupsToRemove" value=","/>
	<g:hiddenField name="fieldsToAdd" value=","/>
	<g:hiddenField name="fieldsToRemove" value=","/>
	<div id="action-buttons" class="buttons">
		<g:if test="${contactInstance?.id}">
			<g:actionSubmit class="btn" id="update-single" action="update" value="${g.message(code:'contact.save')}" disabled="disabled"/>
			<g:link class="cancel btn" disabled="disabled"><g:message code="contact.cancel"/></g:link>
		</g:if>
		<g:else>
			<g:actionSubmit class="btn" action="saveContact" value="${g.message(code:'contact.save')}"/>
			<g:link class="cancel btn" action="index" default="Cancel"><g:message code="contact.cancel"/></g:link>
		</g:else>
		
		<g:if test="${contactInstance?.id}">
			<a id="btn_delete" onclick="launchConfirmationPopup(i18n('smallpopup.contact.delete.title'));" class="btn">
				<g:message code="contact.delete"/>
			</a>
		</g:if>
	</div>

	<fsms:input instance="${contactInstance}" field="name"/>
	<fsms:input instance="${contactInstance}" field="mobile">
		<g:if test="${contactInstance?.mobile?.trim()}">
			<a class="remove-field" id="remove-contactmobile"/>
			<g:remoteLink class="send-message" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.mobile]" onSuccess="launchMediumWizard(i18n('wizard.send.message.title'), data, i18n('wizard.send'), true);">&nbsp;
			</g:remoteLink>
		</g:if>
	</fsms:input>
	<fsms:input instance="${contactInstance}" field="email" class="email">
		<g:if test="${contactInstance?.email?.trim() && contactInstance?.validate(['email', contactInstance?.email])}">
			<a class="remove-field" id="remove-contactemail"></a>
		</g:if>
	</fsms:input>
	<div id='custom-list' class="field">
		<ul id="custom-field-list">
			  <g:each in="${contactFieldInstanceList}" status="i" var="f">
				  <li class="${f == fieldInstance ? 'selected' : ''}">
					  <label for="custom-field-${f.name}">${f.name}</label>
					  <input type="text" name="${f.name}" id="field-item-${f.name}" value="${f.value}"/>
					  <a class="remove-field" id="remove-field-${f.id}"></a>
				  </li>
			  </g:each>
		</ul>
	</div>
	<div id='info-add' class="field">
		<select class="dropdown" id="new-field-dropdown" name="new-field-dropdown">
			<option class="not-field" value="na"><g:message code="contact.customfield.addmoreinformation"/></option>
			<option class="predefined-field" value="Street address"><g:message code="contact.customfield.streetaddress"/></option>
			<option class="predefined-field" value="City"><g:message code="contact.customfield.city"/></option>
			<option class="predefined-field" value="Postcode"><g:message code="contact.customfield.postcode"/></option>
			<option class="predefined-field" value="State"><g:message code="contact.customfield.state"/></option>
			<g:each in="${uniqueFieldInstanceList}" status="i" var="f">
				<g:if test="${f != 'Street address' && f != 'City' && f != 'Postcode' && f != 'State'}">
					<option value="${f}">${f}</option>
				</g:if>
			</g:each>
			<option class="create-custom-field" value='add-new'><g:message code="contact.customfield.option.createnew"/></option>
		</select>
	</div>
	<div id='note-area' class="field">
		<label for="notes"><g:message code="contact.notes.label"/></label>
		<g:textArea name="notes" id="notes" value="${contactInstance?.notes}"/>
	</div>
	<div id="group-section" class="field">
		<label for="groups"><g:message code="contact.groups.label"/></label>
		<div>
			<ol id='group-list'>
				<g:each in="${contactGroupInstanceList}" status="i" var="g">
					<li class="${g == groupInstance ? 'selected' : ''}" id="${g.name}">
						<span>${g.name}</span><a class="remove-group" id="remove-group-${g.id}"></a>
					</li>
				</g:each>
				<li id="no-groups" style="${contactGroupInstanceList?'display: none':''}">
					<p><g:message code="contact.notinanygroup.label"/></p>
				</li>
			</ol>
		</div>
	</div>
	<div id='group-add' class="field">
		<select id="group-dropdown" name="group-dropdown">
			<option class="not-group"><g:message code="contact.add.to.group"/></option>
			<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
				<option value="${g.id}">${g.name}</option>
			</g:each>
		</select>
	</div>
	<div id='message-stats'>
		<label for="messages"><g:message code="contact.messages.label"/></label>
		<div id="message-count">
			<p id="num-sent">${contactInstance?.outboundMessagesCount} <g:message code="contact.sent.messages"/></</p>
			<p id="num-recieved">${contactInstance?.inboundMessagesCount} <g:message code="contact.received.messages"/></</p>
		</div>
		<div id="contact-msg-search">
			<g:link class="btn" controller='search' action='result' params="[contactString: contactInstance?.name]" >
				<span id="search-image"><g:message code="contact.search.messages"/></span>
			</g:link>
		</div>
	</div>
</div>
<r:script>
	$(document).ready(function(){
		$('#group-dropdown').live("change", function(){
				$('select').selectmenu();
			});
		setInterval(refreshMessageStats, 15000);
		function refreshMessageStats(data) {
			var url = 'contact/getMessageStats'
			var numSent = $('#num-sent')
			var numRecieved = $('#num-recieved')
			$.getJSON(url_root + url, {id: "${contactInstance?.id}"},function(data) {
				numSent.text(numSent.text().replace(/\d{1,}/, data.outboundMessagesCount))
				numRecieved.text(numRecieved.text().replace(/\d{1,}/, data.inboundMessagesCount))
			});
			
		}

		$("#details").validate({
			rules: {
				contactmobile: {
					remote: {
						url: "${createLink(controller:'contact', action:'checkForDuplicates')}",
						data: {contactId:"${contactInstance?.id}"}
					}
				}
			},
			messages: {
				contactmobile: {
					remote: "${message(code: 'contact.exists.prompt')}"
				}
			}
		});
	});
		

</r:script>
