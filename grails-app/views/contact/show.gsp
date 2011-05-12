<%@ page import="frontlinesms2.Contact" %>
<html>
    <head>
        <meta name="layout" content="contacts" />
		<g:javascript library="jquery" plugin="jquery"/>
		<script type="text/javascript">
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
			<g:hiddenField name="id" value="${contactInstance?.id}"/>
			<g:hiddenField name="version" value="${contactInstance?.version}"/>
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
			</div>
			<ol id="group-list">
				<g:each in="${contactGroupInstanceList}" status="i" var="g">
					<li class="${g == groupInstance ? 'selected' : ''}">
						<h2>${g.name}</h2>
						<a class="remove-group" id="remove-group-${g.id}">Delete</a>
					</li>
				</g:each>
			  	<li id="no-groups" style="display: none">
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
			<div class="buttons">
				<g:actionSubmit class="update" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
			</div>
		</g:form>
    </body>
</html>
