<%@ page import="frontlinesms2.Group" %>
<h2><g:message code="subscription.group.header"/></h2>
<div class="info">
	<p><g:message code="subscription.group.description"/></p>
</div>
<div class="input">
	<g:select name="subscriptionGroup" class="dropdown notEmpty"
		noSelection="${activityInstanceToEdit? [(activityInstanceToEdit.group.id) : (activityInstanceToEdit.group.name)] : ['': g.message(code:'subscription.group.none.selected')] }"
		from="${Group.getAll()}" optionKey="id" optionValue="name" />
		<!--
			TODO: the group list needs to exclude currently selected group
		-->
</div>

<h2><g:message code="subscription.keyword.header"/></h2>
<div class="input">
	<g:textField name="keyword" id="subscription-keyword" value="${activityInstanceToEdit?.keyword?.value}" class="required"/>
</div>


