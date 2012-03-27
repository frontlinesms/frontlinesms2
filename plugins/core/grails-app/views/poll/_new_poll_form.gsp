<g:formRemote url="[action: 'save', controller:'poll', params: [ownerId:activityInstanceToEdit?.id ?: null]]" name='new-poll-form' method="post" onSuccess="launchMediumPopup('Poll ${activityInstanceToEdit ? 'updated': 'created'}!', data, 'Ok', summaryRedirect)">
	<g:render template="/poll/question" plugin="${grailsApplication.config.frontlinesms2.plugin}"/>
	<g:render template="/poll/responses"/>
	<g:render template="/poll/sorting"/>
	<g:render template="/poll/replies"/>
	<g:render template="/poll/message"/>
	<div id="tabs-6">
		<g:render template="/quickMessage/select_recipients" model= "['contactList' : contactList,
		                                                                'groupList': groupList,
		                                                                'nonExistingRecipients': [],
		                                                                'recipients': []]"/>
	</div>
	<g:render template="/poll/confirm"/>
</g:formRemote>
