<g:if test="${groupInstanceList || pollInstanceList}">
	<h2>Limit Search to:</h2>
	<ol class="sub-menu">
		<li class='field'>
			<g:select name="groupId" from="${groupInstanceList}" value="${groupInstance?.id}"
					  optionKey="id" optionValue="name"
					  noSelection="${['':'Select group']}"/>
		</li>
		<li class="field">
			<g:select name="activityId" from="${pollInstanceList + folderInstanceList}"
					  value="${activityId}"
					  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
					  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.name}}"
					  noSelection="${['':'Select activity / folder']}"/>
		</li>
		<li class="field">
			<g:select name="messageStatus" from="${['All sent and received', 'Only received messages', 'Only sent messages']}"
					value="${messageStatus}"
					keys="${['', 'INBOUND', 'SENT, SEND_PENDING, SEND_FAILED']}"/>
		</li>
	</ol>
</g:if>