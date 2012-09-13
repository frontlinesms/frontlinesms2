<ul class="info">
	<h1>
		<g:message code="${ownerInstance.shortName}.title" args="${[ownerInstance.name]}"/>
	</h1>
	<li>
		<g:message code="subscription.info.group" args="${[ownerInstance.group.name]}"/>
	</li>
	<li>
		<g:message code="subscription.info.groupMemberCount" args="${[ownerInstance.group.members.size()]}"/>
	</li>
	<li>
		<g:message code="subscription.info.keyword" args="${[ownerInstance.keyword.value]}"/>
	</li>
	<li>
		<g:message code="subscription.info.joinAliases" args="${[ownerInstance.joinAliases]}"/>
	</li>
	<li>
		<g:message code="subscription.info.leaveAliases" args="${[ownerInstance.leaveAliases]}"/>
	</li>
</ul>

<div class="controls">
	<g:link controller="group" action="show" id="${ownerInstance.group.id}" class="btn">
		<g:message code="subscription.group.goto"/>
	</g:link>
</div>

