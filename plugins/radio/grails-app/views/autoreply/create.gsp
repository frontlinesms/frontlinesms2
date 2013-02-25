<meta name="layout" content="popup"/>
<fsms:wizard url="[action:'save', controller:'autoreply', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]" name="create_autoreply" method="post" onSuccess="checkForSuccessfulSave(data, i18n('autoreply.label'))"
		verticalTabs="radioShow.selectShow,
				autoreply.create.message,
				activity.generic.sorting,
				autoreply.confirm"
		templates="/radioShow/selectShow,
				/message/compose,
				/activity/generic/sorting,
				/autoreply/confirm,
				/autoreply/save,
				/autoreply/validate"/>