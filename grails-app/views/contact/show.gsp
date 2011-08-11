<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="contacts" />
		<g:javascript src="contact/show-groups.js"></g:javascript>
		<g:javascript src="contact/show-fields.js"></g:javascript>
		<script type="text/javascript">
			$(function() {
				$('input[name="name"]').focus();
			});
		</script>
    </head>
    <body>
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
				<g:link class="cancel" action="list" default="Cancel">Cancel</g:link>
				<g:if test="${contactInstance.id}">
					<g:actionSubmit class="update" action="update" value="${message(code: 'default.button.save.label', default: 'Save')}"/>
					<g:actionSubmit class="delete" action="deleteContact" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('Delete ${contactInstance.name}')"/>
				</g:if>
			  	<g:else>
					<g:actionSubmit class="save" action="saveContact" value="${message(code: 'default.button.save.label', default: 'Save')}"/>
			  	</g:else>
			</div>
			<div id="contact-info">
				<div class="field">
					<label for="name"><g:message code="contact.name.label" default="Name"/></label>
					<g:textField name="name" id="name" value="${contactInstance?.name}"/>
				</div>
				<div class="field">
					<label for="primaryMobile"><g:message code="contact.primaryMobile.label" default="Mobile (Primary)"/></label>
					<g:textField name="primaryMobile" id="primaryMobile" value="${contactInstance?.primaryMobile}"/>
					<g:if test="${contactInstance?.primaryMobile}">
						  <g:remoteLink controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3',
						  recipients:  contactInstance.primaryMobile]" onSuccess="launchWizard('Send Message', data);" class="send-message">
							  Send Message
						  </g:remoteLink>
					</g:if>
				</div>
				<div class="field">
					<label for="secondaryMobile"><g:message code="contact.secondaryMobile.label" default="Other Mobile"/></label>
					<g:textField name="secondaryMobile" id="secondaryMobile" value="${contactInstance?.secondaryMobile}"/>
					<g:if test="${contactInstance?.secondaryMobile}">
					<g:remoteLink controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients:  contactInstance.secondaryMobile]" onSuccess="launchWizard('Send Message',data);" class="send-message">
							  Send Message
						</g:remoteLink>
					</g:if>
				</div>
			   <div class="field">
					<label for="email"><g:message code="contact.email.label" default="Email"/></label>
					<g:textField name="email" id="email" value="${contactInstance?.email}"/>
					<g:if test="${contactInstance?.email && contactInstance.validate(['email', contactInstance?.email])}">
						<g:remoteLink controller="quickMessage" action="create" params="[recipients:  contactInstance.email]" onSuccess="loadContents(data);" class="quick_message">
							Send Message
						</g:remoteLink>
					</g:if>
				</div>
				<ol id="custom-field-list">
				  <g:each in="${contactFieldInstanceList}" status="i" var="f">
					  <li class="${f == fieldInstance ? 'selected' : ''}">
						  <label for="custom-field-${f.name}">${f.name}</label>
						  <input type="text" name="${f.name}" id="field-item-${f.name}" value="${f.value}"/>
						  <a class="remove-field" id="remove-field-${f.id}">Delete</a>
					  </li>
				  </g:each>
				</ol>
			</div>
			<div class="field">
				<select id="new-field-dropdown" name="new-field-dropdown">
					<option class="not-field" value="na">Add more information...</option>
					<option class="create-custom-field" value='add-new'>Create custom field</option>
					<g:each in="${uniqueFieldInstanceList}" status="i" var="f">
						<option value="${f}">${f}</option>
					</g:each>
				</select>
				<div class="field">
					<label for="notes"><g:message code="contact.notes.label" default="Notes"/></label>
					<g:textArea name="notes" id="notes" value="${contactInstance?.notes}"/>
				</div>
			</div>
			<ol id="group-list">
				<g:each in="${contactGroupInstanceList}" status="i" var="g">
					<li class="${g == groupInstance ? 'selected' : ''}">
						<h2>${g.name}</h2>
						<a class="remove-group" id="remove-group-${g.id}">Delete</a>
					</li>
				</g:each>
				<li id="no-groups" style="${contactGroupInstanceList?'display: none':''}">
					<p>Not part of any Groups</p>
				</li>
			</ol>
			<div class="field">
				<select id="group-dropdown" name="group-dropdown">
					<option class="not-group">Add to group...</option>
					<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
						<option value="${g.id}">${g.name}</option>
					</g:each>
				</select>
			</div>
			<g:if test="${contactInstance.id}">
				<div id="message-count">
					<h3>Messages</h3>
					<p>${contactInstance.inboundMessagesCount} messages sent</p>
					<p>${contactInstance.outboundMessagesCount} messages received</p>
				</div>
			</g:if>
		</g:form>
    </body>
</html>
