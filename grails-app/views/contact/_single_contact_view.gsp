<div class="single-contact">
	<div class="buttons">
		<ol>
			<g:if test="${contactInstance?.id}">
				<li><g:actionSubmit id="update-single" action="update" value="Save"/></li>
			</g:if>
			<g:else>
				<li><g:actionSubmit id="save-new" action="saveContact" value="Save"/></li>
			</g:else>
			<li><g:link class="cancel" action="list" default="Cancel">Cancel</g:link></li>
			<g:if test="${contactInstance?.id}">
				<li>
					<a id="btn_delete" onclick="launchConfirmationPopup('Delete');">
						Delete
					</a>
				</li>
			</g:if>
		</ol>
	</div>
	
	<div class="basic-info field">
		<label for="name"><g:message code="contact.name.label" default="Name"/></label>
		<g:textField name="name" id="name" value="${contactInstance?.name}"/>
	</div>
	<div class="basic-info field">
		<label for="primaryMobile"><g:message code="contact.primaryMobile.label" default="Mobile (Primary)"/></label>
		<g:textField name="primaryMobile" id="primaryMobile" value="${contactInstance?.primaryMobile?.trim()}"/>
		<g:if test="${contactInstance?.primaryMobile?.trim()}">
			<a class="remove-field" id="remove-primaryMobile"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
			<g:remoteLink class="send-message" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.primaryMobile]" onSuccess="launchMediumWizard('Send Message', data, 'Send', null, true, null, true);addTabValidations();">
				<img src='${resource(dir:'images/icons',file:'send.gif')}' />
			</g:remoteLink>
		</g:if>
	</div>
	<div class="basic-info field">
		<label for="secondaryMobile"><g:message code="contact.secondaryMobile.label" default="Other Mobile"/></label>
		<g:textField name="secondaryMobile" id="secondaryMobile" value="${contactInstance?.secondaryMobile?.trim()}"/>
		<g:if test="${contactInstance?.secondaryMobile?.trim()}">
			<a class="remove-field" id="remove-secondaryMobile"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
			<g:remoteLink class="send-message" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.secondaryMobile]" onSuccess="launchMediumWizard('Send Message', data, 'Send', null, true);addTabValidations();">
				<img src='${resource(dir:'images/icons',file:'send.gif')}' />
			</g:remoteLink>
		</g:if>
	</div>
   	<div class="basic-info field">
		<label for="email"><g:message code="contact.email.label" default="Email"/></label>
		<g:textField name="email" id="email" value="${contactInstance?.email?.trim()}"/>
		<g:if test="${contactInstance?.email?.trim() && contactInstance?.validate(['email', contactInstance?.email])}">
			<a class="remove-field" id="remove-email"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
			<g:remoteLink controller="quickMessage" action="create" params="[recipients:  contactInstance?.email]" onSuccess="loadContents(data);" class="quick_message">
				 <img src='${resource(dir:'images/icons',file:'send.gif')}' />
			</g:remoteLink>
		</g:if>
	</div>
	<div id='custom-list' class="field">
		<ol id="custom-field-list">
			  <g:each in="${contactFieldInstanceList}" status="i" var="f">
				  <li class="${f == fieldInstance ? 'selected' : ''}">
					  <label for="custom-field-${f.name}">${f.name}</label>
					  <input type="text" name="${f.name}" id="field-item-${f.name}" value="${f.value}"/>
					  <a class="remove-field" id="remove-field-${f.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
				  </li>
			  </g:each>
		</ol>
	</div>
	<div id='info-add' class="dropdown">
		<select id="new-field-dropdown" name="new-field-dropdown">
			<option class="not-field" value="na">Add more information...</option>
			<option class="create-custom-field" value='add-new'>Create custom field</option>
			<g:each in="${uniqueFieldInstanceList}" status="i" var="f">
				<option value="${f}">${f}</option>
			</g:each>
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
				<li class="${g == groupInstance ? 'selected' : ''}">
					<input type="text" name="${g.name}" value="${g.name}" readonly="readonly" />
					<a class="remove-group" id="remove-group-${g.id}"><img class='remove' src='${resource(dir:'images/icons',file:'remove.gif')}' /></a>
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
			<img src='${resource(dir:'images/icons',file:'sentmessages.gif')}' />
			<p>${contactInstance?.inboundMessagesCount} messages sent</p><br />
			<img src='${resource(dir:'images/icons',file:'recievedmessages.gif')}' />
			<p>${contactInstance?.outboundMessagesCount} messages received</p>
		</div>
		<div id='message-search'>
			<g:link class="buttons" controller='search' action='result' params="[contactSearchString: contactInstance?.name]" >
				<img src='${resource(dir:'images/icons',file:'search.gif')}' />
				Search for messages
			</g:link>
		</div>
	</div>
</div>
