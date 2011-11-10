<div class="single-contact">
<div id="action-buttons" class="buttons">
			<g:if test="${contactInstance?.id}">
				<g:actionSubmit id="update-single" action="update" value="Save" disabled="disabled"/>
				<input type="button" class="cancel" value="Cancel" disabled="disabled"/>
			</g:if>
			<g:else>
				<g:actionSubmit id="save-new" action="saveContact" value="Save"/>
				<g:link class="cancel" action="list" default="Cancel">Cancel</g:link>
			</g:else>
			
			<g:if test="${contactInstance?.id}">
					<a id="btn_delete" onclick="launchConfirmationPopup('Delete');" class="btn">
						Delete
					</a>
			</g:if>
</div>

		<div class="basic-info form-field field">
		<label for="name"><g:message code="contact.name.label" default="Name"/></label>
		<g:textField name="name" id="name" value="${contactInstance?.name}"/>
	</div>
	<div class="basic-info field">
		<label for="primaryMobile"><g:message code="contact.primaryMobile.label" default="Mobile (Primary)"/></label>
		<g:textField name="primaryMobile" id="primaryMobile" value="${contactInstance?.primaryMobile?.trim()}"/>
		<g:if test="${contactInstance?.primaryMobile?.trim()}">
			<a class="remove-field" id="remove-primaryMobile"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
			<g:remoteLink class="send-message" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.primaryMobile]" onSuccess="launchMediumWizard('Send Message', data, 'Send', null, true, null, true);addTabValidations();">
				<img src='${resource(dir:'images/icons',file:'send.png')}' />
			</g:remoteLink>
		</g:if>
	</div>
	<div class="basic-info form-field field">
		<label for="secondaryMobile"><g:message code="contact.secondaryMobile.label" default="Other Mobile"/></label>
		<g:textField name="secondaryMobile" id="secondaryMobile" value="${contactInstance?.secondaryMobile?.trim()}"/>
		<g:if test="${contactInstance?.secondaryMobile?.trim()}">
			<a class="remove-field" id="remove-secondaryMobile"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
			<g:remoteLink class="send-message" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.secondaryMobile]" onSuccess="launchMediumWizard('Send Message', data, 'Send', null, true);addTabValidations();">
				<img src='${resource(dir:'images/icons',file:'send.png')}' />
			</g:remoteLink>
		</g:if>
	</div>
   	<div class="basic-info field form-field">
		<label for="email"><g:message code="contact.email.label" default="Email"/></label>
		<g:textField name="email" id="email" value="${contactInstance?.email?.trim()}"/>
		<g:if test="${contactInstance?.email?.trim() && contactInstance?.validate(['email', contactInstance?.email])}">
			<a class="remove-field" id="remove-email"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
			<g:remoteLink controller="quickMessage" action="create" params="[recipients:  contactInstance?.email]" onSuccess="loadContents(data);" class="quick_message">
				 <img src='${resource(dir:'images/icons',file:'send.png')}' />
			</g:remoteLink>
		</g:if>
	</div>
	<div id='custom-list' class="field">
		<ol id="custom-field-list">
			  <g:each in="${contactFieldInstanceList}" status="i" var="f">
				  <li class="${f == fieldInstance ? 'selected' : ''}">
					  <label for="custom-field-${f.name}">${f.name}</label>
					  <input type="text" name="${f.name}" id="field-item-${f.name}" value="${f.value}"/>
					  <a class="remove-field" id="remove-field-${f.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
				  </li>
			  </g:each>
		</ol>
	</div>
	<div id='info-add' class="dropdown">
		<select id="new-field-dropdown" name="new-field-dropdown">
			<option class="not-field" value="na">Add more information...</option>
			<option class="predefined-field" value="Street address">Street address</option>
			<option class="predefined-field" value="City">City</option>
			<option class="predefined-field" value="Postcode">Postcode</option>
			<option class="predefined-field" value="State">State</option>
			<g:each in="${uniqueFieldInstanceList}" status="i" var="f">
				<g:if test="${f != 'Street address' && f != 'City' && f != 'Postcode' && f != 'State'}">
					<option value="${f}">${f}</option>
				</g:if>
			</g:each>
			<option class="create-custom-field" value='add-new'>Create new...</option>
		</select>
	</div>
	<div id='note-area' class="field">
		<label for="notes">Notes</label>
		<g:textArea name="notes" id="notes" value="${contactInstance?.notes}"/>
	</div>
	<div id="group-section" class="field">
	<label for="groups">Groups</label>
	<div>
		<ol id='group-list'>
			<g:each in="${contactGroupInstanceList}" status="i" var="g">
				<li class="${g == groupInstance ? 'selected' : ''}" id="${g.name}">
					<span>${g.name}</span><a class="remove-group" id="remove-group-${g.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
				</li>
			</g:each>
			<li id="no-groups" style="${contactGroupInstanceList?'display: none':''}">
				<p>Not part of any Groups</p>
			</li>
		</ol>
	</div>
	</div>
	<div id='group-add' class="dropdown">
		<select id="group-dropdown" name="group-dropdown">
			<option class="not-group">Add to group...</option>
			<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
				<option value="${g.id}">${g.name}</option>
			</g:each>
		</select>
	</div>
	<div id='message-stats' class="field">
		<label for="messages">Messages</label>
		<div id="message-count">
			<img src='${resource(dir:'images/icons',file:'sentmessages.png')}' />
			<p>${contactInstance?.inboundMessagesCount} messages sent</p><br />
			<img src='${resource(dir:'images/icons',file:'recievedmessages.png')}' />
			<p>${contactInstance?.outboundMessagesCount} messages received</p>
		</div>
		<div id='message-search'>
			<g:link class="buttons" controller='search' action='result' params="[contactSearchString: contactInstance?.name]" >
				<img src='${resource(dir:'images/icons',file:'search.png')}' />
				Search for messages
			</g:link>
		</div>
	</div>
</div>

<g:javascript>

	$(function() {
		$(".buttons .cancel").click(function() {
			window.location = window.location
		});
	});
	
	$("div.single-contact").keyup(function(event) {
		enableSaveAndCancel()
	});

	$("a.remove-field").click(function(event) {
		enableSaveAndCancel()
	});
	
	function enableSaveAndCancel() {
		$("#update-single").attr("disabled", false);
		$("#update-all").attr("disabled", false);
		$(".buttons .cancel").attr("disabled", false);
	}
</g:javascript>
