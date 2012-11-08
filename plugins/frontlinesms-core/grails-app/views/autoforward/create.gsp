<meta name="layout" content="popup"/>
<fsms:wizard url="[action:'save', controller:'autoforward', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]" name="create_autoforward" method="post" onSuccess="checkForSuccessfulSave(data, i18n('autoforward.label'))"
		verticalTabs="autoforward.create.message,
				activity.generic.sorting,
				autoforward.recipients,
				autoforward.confirm"
		templates="/message/compose,
				/activity/generic/sorting,
				/message/select_recipients,
				/autoforward/confirm,
				/autoforward/save,
				/autoforward/validate"/>
