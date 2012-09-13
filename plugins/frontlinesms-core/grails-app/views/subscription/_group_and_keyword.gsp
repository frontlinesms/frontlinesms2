<%@ page import="frontlinesms2.Group" %>
<h2><g:message code="subscription.group.header"/></h2>
<div class="info">
	<p><g:message code="subscription.group.description"/></p>
</div>
<div class="input">
	<g:select name="subscriptionGroup" class="dropdown notEmpty"
		noSelection="${activityInstanceToEdit? null : ['': g.message(code:'subscription.group.none.selected')] }"
		from="${Group.getAll()}" optionKey="id" optionValue="name"
		value="${activityInstanceToEdit?.group?.id}" />
		<!--
			TODO: the group list needs to exclude currently selected group
		-->
</div>

<h2><g:message code="subscription.keyword.header"/></h2>
<div class="input">
	<g:textField name="keyword" value="${activityInstanceToEdit?.keyword?.value}" class="required"/>
</div>


