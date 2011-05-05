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

				addIdToGroupHiddenField(groupId);
			}

			function removeGroupClickAction() {
				var me = $(this);
				var groupId = me.attr('id').substring('remove-group-'.length);
				var groupName = me.parent().children('h2').text();

				var option = $("<option value='" + groupId + "'>" + groupName + '</option>');
				option.click(addGroupClickAction);

				$('#group-dropdown').append(option);
				me.parent().remove();

				removeIdFromGroupHiddenField(groupId);
			}

			function removeIdFromGroupHiddenField(groupId) {
				var f = getGroupHiddenField();
				var oldList = f.val();
				var newList = oldList.replace(','+ groupId +',', ',');
				f.val(newList);
			}
			function addIdToGroupHiddenField(groupId) {
				var f = getGroupHiddenField();
				var oldList = f.val();
				var newList = oldList + groupId + ',';
				f.val(newList);
			}
			function getGroupHiddenField() {
				return $('input:hidden[name=groups]');
			}
		</script>
    </head>
    <body>
		<g:form name="contact-details">
			<g:hiddenField name="id" value="${contactInstance?.id}"/>
			<g:hiddenField name="version" value="${contactInstance?.version}"/>
			<g:hiddenField name="groups" value="${contactGroupInstanceListString}"/>

			<div id="contact-info">
				<div>
					<label for="name"><g:message code="contact.name.label" default="Name"/></label>
					<g:textField name="name" id="name" value="${contactInstance?.name}"/>
				</div>
				<div>
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
			</ol>
			<div>
				<select id="group-dropdown" name="group-dropdown">
					<option disabled="disabled" class="not-group">Add to group...</option>
					<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
						<option value="${g.id}">${g.name}</option>
					</g:each>
				</select>
			</div>
			<div class="buttons">
				<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
			</div>
		</g:form>
    </body>
</html>
