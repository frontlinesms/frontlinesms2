<g:formRemote url="[action: 'save', controller:'poll', params: [ownerId:activityInstanceToEdit?.id ?: null]]" name='new-poll-form' method="post" onSuccess="checkForSuccessfulSave(data, 'Poll')">
	<g:render template="../poll/question" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
	<g:render template="../poll/responses" plugin="core"/>
	<g:render template="../poll/sorting" plugin="core"/>
	<g:render template="../poll/replies" plugin="core"/>
	<g:render template="../poll/message" plugin="core"/>
	<div id="tabs-6">
		<g:render template="../quickMessage/select_recipients" plugin="core" model= "['contactList' : contactList,
		                                                                'groupList': groupList,
		                                                                'nonExistingRecipients': [],
		                                                                'recipients': []]"/>
	</div>
	<g:render template="../poll/confirm" plugin="core"/>
</g:formRemote>
