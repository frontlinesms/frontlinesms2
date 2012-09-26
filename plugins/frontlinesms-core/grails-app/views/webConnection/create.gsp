<%@ page contentType="text/html;charset=UTF-8" %>
<head>
	<meta name="layout" content="popup"/>
	<link href="${r.resource(dir:'css/activity', file:'webconnection.css')}" media="screen, projection" rel="stylesheet" type="text/css"/>
</head>
<body>
	<fsms:wizard url="[action: 'save', controller:'webConnection', params: [ownerId:activityInstanceToEdit?.id ?: null, format: 'json']]" name='new-webconnection-form' method="post" onSuccess="checkForSuccessfulSave(data, i18n('webConnection.label') )"
			verticalTabs="webConnection.type,
					webConnection.configure,
					webConnection.sorting,
					webConnection.confirm"
			templates="/webConnection/type,
					/webConnection/configure,
					/webConnection/sorting,
					/webConnection/confirm,
					/webConnection/save,
					/webConnection/validate"/>
</body>
