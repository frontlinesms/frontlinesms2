<g:formRemote url="${[action:activityInstanceToEdit ? 'edit':'save', controller:'poll', id:activityInstanceToEdit?.id]}" name='new-poll-form' method="post" onSuccess="launchMediumPopup('Poll ${activityInstanceToEdit ? 'updated': 'created'}!', data, 'Ok', summaryRedirect)">
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
