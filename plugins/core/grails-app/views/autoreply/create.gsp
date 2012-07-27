<meta name="layout" content="popup"/>
<fsms:wizard url="[action:'save', controller:'autoreply', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]" name="create_autoreply" method="post" onSuccess="checkForSuccessfulSave(data, i18n('autoreply.label'))"
		verticalTabs="autoreply.enter.keyword,
				autoreply.create.message,
				autoreply.confirm"
		templates="/autoreply/keyword,
				/message/compose,
				/autoreply/confirm,
				/autoreply/save,
				/autoreply/validate"/>