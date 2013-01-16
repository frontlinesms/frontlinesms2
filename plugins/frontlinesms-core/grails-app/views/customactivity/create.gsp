<meta name="layout" content="popup"/>
<fsms:wizard url="[action:'save', controller:'customActivity', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]" name="create_customactivity" method="post" onSuccess="checkForSuccessfulSave(data, i18n('customactivity.label'))"
		verticalTabs="activity.generic.sorting,
				customactivity.config,
				customactivity.confirm"
		templates="/activity/generic/sorting,
				/customactivity/config,
				/customactivity/confirm,
				/customactivity/save,
				/customactivity/validate"/>
