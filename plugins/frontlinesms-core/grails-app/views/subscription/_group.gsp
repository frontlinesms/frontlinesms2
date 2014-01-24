<%@ page import="frontlinesms2.Group" %>
<h2><g:message code="subscription.select.group"/></h2>
<div class="info">
	<p><g:message code="subscription.group.description"/></p>
</div>
<div class="input">
	<g:select name="subscriptionGroup" class="dropdown not-empty select-group"
		noSelection="${activityInstanceToEdit? null : ['': g.message(code:'subscription.group.none.selected')] }"
		from="${Group.getAll() + [id:'create_group', name:'Create group']}" optionKey="id" optionValue="name"
		value="${activityInstanceToEdit?.group?.id}" />
		<!--
			TODO: the group list needs to exclude currently selected group
		-->
</div>

<div class="input hide add-group">
	<label for="group-name">
		<g:message code="contact.create.group" />
	</label>
	<div>
		<g:textField name="groupName" value="" class="unique-group"/>
		<a class="btn create-group">
			<g:message code="action.save" />
		</a>
	</div>
</div>
