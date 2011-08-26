<div class="single-contact">
	<g:form name="contact-details">
		<g:hiddenField name="contactId" value="${contactInstance?.id}"/>
		<g:hiddenField name="version" value="${contactInstance?.version}"/>
		<g:if test="${contactsSection instanceof frontlinesms2.Group}">
			<g:hiddenField name="groupId" value="${contactsSection.id}"/>
		</g:if>
		<g:hiddenField name="groupsToAdd" value=","/>
		<g:hiddenField name="groupsToRemove" value=","/>
		<g:hiddenField name="fieldsToAdd" value=","/>
		<g:hiddenField name="fieldsToRemove" value=","/>
		<div class="buttons">
			<ol>
				<g:if test="${contactInstance.id}">
					<li><g:actionSubmit class="update" action="update" value="${message(code: 'default.button.save.label', default: 'Save')}"/></li>
				</g:if>
				<g:else>
					<li><g:actionSubmit class="save" action="saveContact" value="${message(code: 'default.button.save.label', default: 'Save')}"/></li>
				</g:else>
				<li><g:link class="cancel" action="list" default="Cancel">Cancel</g:link></li>
				<g:if test="${contactInstance.id}">
					<li><g:actionSubmit id="btn_delete" class="delete" action="deleteContact" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('Delete ${contactInstance.name}')"/></li>
				</g:if>
			</ol>
		</div>
		<div class="basic-info field">
			<label for="name"><g:message code="contact.name.label" default="Name"/></label>
			<g:textField name="name" id="name" value="${contactInstance?.name}"/>
		</div>
		<div class="basic-info field">
			<label for="primaryMobile"><g:message code="contact.primaryMobile.label" default="Mobile (Primary)"/></label>
			<g:textField name="primaryMobile" id="primaryMobile" value="${contactInstance?.primaryMobile}"/>
			<g:if test="${contactInstance?.primaryMobile}">
				<g:remoteLink controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.primaryMobile]" onSuccess="launchMediumWizard('Send Message', data, 'Send');" class="send-message">
					<img src='${resource(dir:'images/icons',file:'send.gif')}' />
				</g:remoteLink>
			</g:if>
		</div>
		<div class="basic-info field">
			<label for="secondaryMobile"><g:message code="contact.secondaryMobile.label" default="Other Mobile"/></label>
			<g:textField name="secondaryMobile" id="secondaryMobile" value="${contactInstance?.secondaryMobile}"/>
			<g:if test="${contactInstance?.secondaryMobile}">
				<g:remoteLink controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.secondaryMobile]" onSuccess="launchMediumWizard('Send Message', data, 'Send');" class="send-message">
					<img src='${resource(dir:'images/icons',file:'send.gif')}' />
				</g:remoteLink>
			</g:if>
		</div>
	   	<div class="basic-info field">
			<label for="email"><g:message code="contact.email.label" default="Email"/></label>
			<g:textField name="email" id="email" value="${contactInstance?.email}"/>
			<g:if test="${contactInstance?.email && contactInstance.validate(['email', contactInstance?.email])}">
				<g:remoteLink controller="quickMessage" action="create" params="[recipients:  contactInstance.email]" onSuccess="loadContents(data);" class="quick_message">
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
				<p>${contactInstance.inboundMessagesCount} messages sent</p><br />
				<img src='${resource(dir:'images/icons',file:'recievedmessages.gif')}' />
				<p>${contactInstance.outboundMessagesCount} messages received</p>
			</div>
			<div id='message-search'>
				<g:link class="buttons" controller='search' action='result' params="[contactSearchString: contactInstance?.name]" >
					<img src='${resource(dir:'images/icons',file:'search.gif')}' />
					Search for messages
				</g:link>
			</div>
		</div>
	</g:form>
</div>
