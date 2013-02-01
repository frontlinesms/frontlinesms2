<%@ page contentType="text/html;charset=UTF-8" %>
<head>
	<meta name="layout" content="popup"/>
</head>
<body>
	<fsms:wizard url="[action: 'save', controller:'webconnection', params: [ownerId:activityInstanceToEdit?.id ?: null, format: 'json']]" name='new-webconnection-form' method="post" onSuccess="checkForSuccessfulSave(data, i18n('webconnection.label') )"
			verticalTabs="${activityInstanceToEdit?.id ? '': 'webconnection.type,'}
					webconnection.configure,
					${activityInstanceToEdit?.type == 'ushahidi' ? '': 'webconnection.api,'}
					activity.generic.sorting,
					webconnection.confirm"
			templates="${activityInstanceToEdit?.id ? '': '/webconnection/type,'}
					/webconnection/configure,
					${activityInstanceToEdit?.type == 'ushahidi' ? '': '/webconnection/api,'}
					/activity/generic/sorting,
					/webconnection/confirm,
					/webconnection/save,
					/webconnection/validate"/>
</body>
