<ul class="info">
	<h1>
		<g:message code="announcement.title" args="${[ownerInstance.name]}"/>
	</h1>
	<li>
		<g:formatDate date="${ownerInstance?.dateCreated}"/>
	</li>
	<li>
		<g:message code="fmessage.activity.sentmessage" args="${ [sentMessageCount] }"/>
	</li>
	<li>
		${ownerInstance.sentMessageText}
	</li>
</ul>

