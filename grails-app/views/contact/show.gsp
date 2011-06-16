<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="contacts" />
		<script type="text/javascript">
			$(function() {
			    $('input[name="name"]').focus();
			});
			
			$(document).ready(function() {
  				$("#group-list li a.remove-group").click(removeGroupClickAction);
				$("#group-dropdown").change(addGroupClickAction);
			});

			function addGroupClickAction() {
				var me = $(this).find('option:selected');
				if(me.hasClass('not-group')) return;
				var groupName = me.text();
				var groupId = me.attr('value');

				var groupListItem = $('<li><h2>' + groupName + '</h2></li>');
				var deleteButton = $('<a class="remove-group" id="remove-group-' + groupId + '">Delete</a>');
				deleteButton.click(removeGroupClickAction);
				groupListItem.append(deleteButton);

				$('#group-list').append(groupListItem);
				me.remove();
				$("#no-groups").hide();
				addGroupId(groupId);
				// addIdToGroupHiddenField(groupId);
			}

			function removeGroupClickAction() {
				var me = $(this);
				var groupId = me.attr('id').substring('remove-group-'.length);
				var groupName = me.parent().children('h2').text();

				var option = $("<option value='" + groupId + "'>" + groupName + '</option>');
				option.click(addGroupClickAction);

				$('#group-dropdown').append(option);
				var groupList = me.parent();
				groupList.remove();
				if($('#group-list').children().children('h2').length < 1) {
				    $('#no-groups').show();
				}

				removeGroupId(groupId);
				// removeIdFromGroupHiddenField(groupId);
			}
			
			function removeGroupId(id) {
			  // remove from the ADD list
			  removeIdFromList(id, 'groupsToAdd');	
			  // add to the REMOVE list
			  addIdToList(id, 'groupsToRemove');
			}
			
			function addGroupId(id) {
			  // remove from the REMOVE list
			  removeIdFromList(id, 'groupsToRemove');
			  // add to the ADD list
		  	  addIdToList(id, 'groupsToAdd');
			}
			function removeIdFromList(id, fieldName) {
				var f = $('input:hidden[name=' + fieldName + ']');
				var oldList = f.val();
				var newList = oldList.replace(','+ id +',', ',');
				f.val(newList);
			}
			function addIdToList(id, fieldName) {
				var f = $('input:hidden[name=' + fieldName + ']');
				var oldList = f.val();
				var newList = oldList + id + ',';
				f.val(newList);
			}
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

			<div id="contact-info">
				<div class="field">
					<label for="name"><g:message code="contact.name.label" default="Name"/></label>
					<g:textField name="name" id="name" value="${contactInstance?.name}"/>
				</div>
				<div class="field">
					<label for="address"><g:message code="contact.address.label" default="Address"/></label>
					<g:textField name="address" id="address" value="${contactInstance?.address}"/>
				</div>
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
			<div class="buttons">
				<g:if test="${contactInstance.id}">
					<g:actionSubmit class="update" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
				</g:if>
			  <g:else>
				<g:actionSubmit class="save" action="saveContact" value="${message(code: 'default.button.save.label', default: 'Save')}"/>
			  </g:else>
				<g:link class="cancel" action="list" default="Cancel">Cancel</g:link>
			</div>
		</g:form>
    </body>
</html>
