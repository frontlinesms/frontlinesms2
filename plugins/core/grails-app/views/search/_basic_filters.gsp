<g:if test="${groupInstanceList || pollInstanceList}">
	<h3 class="list-title"><g:message code="search.filter.label"/></h3>
	<g:select class="dropdown" name="groupId" from="${groupInstanceList}" value="${search?.group?.id}"
			optionKey="id" optionValue="name"
			noSelection="${['': g.message(code:'search.filter.group')]}"/>
	<g:select class="dropdown" name="activityId" from="${activityInstanceList + folderInstanceList}"
			value="${search?.activityId}"
			optionKey="${{(it instanceof frontlinesms2.Activity ? 'activity' : 'folder') + '-' + it.id}}"
			optionValue="${{it.name}}"
			noSelection="${['':g.message(code:'search.filter.activities')]}"/>
	<g:select class="dropdown" name="messageStatus"
			from="${[g.message(code:'search.filter.messages.all'), g.message(code:'search.filter.inbox'), g.message(code:'search.filter.sent')]}"
			value="${search?.status}"
			keys="${['', 'inbound', 'outbound']}"/>
	<div class="input">
		<g:checkBox name="inArchive" value="${search ? (search.inArchive ?: null) : true}"/>
		<label for="inArchive"><g:message code="search.filter.archive"/></label>
	</div>
</g:if>
<div class="input">
	<label for="startDate endDate"><g:message code="search.betweendates.label"/></label>
	<fsms:dateRangePicker startDate="${search?.startDate}" endDate="${search?.endDate}"/>
</div>

