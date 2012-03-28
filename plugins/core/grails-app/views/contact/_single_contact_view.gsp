<div class="single-contact">
	<div id="action-buttons" class="buttons">
		<g:if test="${contactInstance?.id}">
			<g:actionSubmit class="btn" id="update-single" action="update" value="Save" disabled="disabled"/>
			<g:link class="cancel btn" disabled="disabled">Cancel</g:link>
		</g:if>
		<g:else>
			<g:actionSubmit class="btn" id="save-new" action="saveContact" value="Save"/>
			<g:link class="cancel btn" action="index" default="Cancel">Cancel</g:link>
		</g:else>
		
		<g:if test="${contactInstance?.id}">
			<a id="btn_delete" onclick="launchConfirmationPopup('Delete');" class="btn">
				Delete
			</a>
		</g:if>
	</div>
	<div class="basic-info">
		<label for="name"><g:message code="contact.name.label" default="Name"/></label>
		<g:textField name="name" id="name" value="${contactInstance?.name}"/>
	</div>
	<div class="basic-info">
		<label for="mobile"><g:message code="contact.mobile.label" default="Mobile"/></label>
		<g:textField class="numberField" name="mobile" id="mobile" value="${contactInstance?.mobile?.trim()}" onkeyup="checkForNonDigits(); checkForDuplicates();" />
		<g:if test="${contactInstance?.mobile?.trim()}">
			<a class="remove-field" id="remove-mobile"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
			<g:remoteLink class="send-message" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.mobile]" onSuccess="launchMediumWizard('Send Message', data, 'Send', true);">
				<img src='${resource(dir:'images/icons',file:'send.png')}' />
			</g:remoteLink>
		</g:if>
	</div>
   	<div class="basic-info">
		<label for="email"><g:message code="contact.email.label" default="Email"/></label>
		<g:textField name="email" id="email" value="${contactInstance?.email?.trim()}"/>
		<g:if test="${contactInstance?.email?.trim() && contactInstance?.validate(['email', contactInstance?.email])}">
			<a class="remove-field" id="remove-email"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
			<g:remoteLink controller="quickMessage" action="create" params="[recipients:  contactInstance?.email]" onSuccess="loadContents(data);" class="quick_message">
				 <img src='${resource(dir:'images/icons',file:'send.png')}' />
			</g:remoteLink>
		</g:if>
	</div>
	<div id='custom-list' class="basic-info">
		<ul id="custom-field-list">
			  <g:each in="${contactFieldInstanceList}" status="i" var="f">
				  <li class="${f == fieldInstance ? 'selected' : ''}">
					  <label for="custom-field-${f.name}">${f.name}</label>
					  <input type="text" name="${f.name}" id="field-item-${f.name}" value="${f.value}"/>
					  <a class="remove-field" id="remove-field-${f.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
				  </li>
			  </g:each>
		</ul>
	</div>
	<div id='info-add' class="basic-info">
		<select class="dropdown" id="new-field-dropdown" name="new-field-dropdown">
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
	<div id='note-area' class="basic-info">
		<label for="notes">Notes</label>
		<g:textArea name="notes" id="notes" value="${contactInstance?.notes}"/>
	</div>
	<div id="group-section" class="basic-info">
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
	<div id='group-add' class="basic-info">
		<select class= "dropdown" id="group-dropdown" name="group-dropdown">
			<option class="not-group">Add to group...</option>
			<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
				<option value="${g.id}">${g.name}</option>
			</g:each>
		</select>
	</div>
	<div id='message-stats'>
		<label for="messages">Messages</label>
		<div id="message-count">
			<p id="num-sent">${contactInstance?.outboundMessagesCount} messages sent</p>
			<p id="num-recieved">${contactInstance?.inboundMessagesCount} messages received</p>
		</div>
		<div id="contact-msg-search">
			<g:link class="btn" controller='search' action='result' params="[contactString: contactInstance?.name]" >
				<span id="search-image">Search for messages</span>
			</g:link>
		</div>
	</div>
</div>
<g:javascript>
	function refreshMessageStats(data) {
		var url = 'contact/getMessageStats'
		var numSent = $('#num-sent')
		var numRecieved = $('#num-recieved')
		$.getJSON(url_root + url, {id: "${contactInstance?.id}"},function(data) {
			numSent.text(numSent.text().replace(/\d{1,}/, data.outboundMessagesCount))
			numRecieved.text(numRecieved.text().replace(/\d{1,}/, data.inboundMessagesCount))
		});
		
	}
	
	$(function() {
		setInterval(refreshMessageStats, 15000);
	});
</g:javascript>
