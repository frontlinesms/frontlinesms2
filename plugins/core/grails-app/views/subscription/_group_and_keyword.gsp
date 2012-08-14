<h2><g:message code="subscription.group.header"/></h2>
<div class="info">
	<p><g:message code="subscription.group.description"/></p>
</div>
<div class="input">
	<g:select name="subscriptionGroup" value="${activityInstanceToEdit?.group}" class="dropdown notEmpty"
		noSelection="${['': g.message(code:'subscription.group.none.selected')]}"
		from="${groupList}" optionKey="id" optionValue="name"/>
</div>

<h2><g:message code="subscription.keyword.header"/></h2>
<div class="input">
	<g:textField name="keyword" id="subscription-keyword" value="${activityInstanceToEdit?.keyword?.value}" class="required"/>
</div>


