<g:if test="${groupInstanceList || pollInstanceList}">
	<h3 class="list-title"><g:message code="search.filter.label"/></h3>
	<fsms:select class="dropdown" name="groupId" from="${groupInstanceList}"
			value="${search?.group?.id}"
			optionKey="id" optionValue="name"
			noSelection="${['': g.message(code:'search.filter.group')]}"/>
	<fsms:select class="dropdown" name="activityId" from="${activityInstanceList + folderInstanceList}"
			value="${search?.activityId}"
			optionKey="${{it instanceof Map? it.key: ((it instanceof frontlinesms2.Activity ? 'activity' : 'folder') + '-' + it.id)}}"
			optionValue="${{it instanceof Map? it.value: (it.name + " " + it.shortName)}}"
			noSelection="${['':g.message(code:'search.filter.activities')]}"/>
	<fsms:select class="dropdown" name="messageStatus"
			from="${[g.message(code:'search.filter.inbox'), g.message(code:'search.filter.sent')]}"
			keys="${['inbound', 'outbound']}"
			value="${search?.status}"
			noSelection="${['':g.message(code:'search.filter.messages.all')]}"/>
	<div class="input">
		<label for="inArchive">
			<g:message code="search.filter.archive"/>
			<g:checkBox name="inArchive" value="${search?.inArchive?:true}" disabled="${search?.activityId}"/>
		</label>
	</div>
</g:if>
<div id="datePicker" class="input">
	<label for="startDate endDate"><g:message code="search.betweendates.label"/></label>
	<fsms:dateRangePicker startDate="${search?.startDate}" endDate="${search?.endDate}"/>
</div>

