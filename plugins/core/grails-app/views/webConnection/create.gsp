<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<fsms:wizard url="[action: 'save', controller:'webConnection', params: [ownerId:activityInstanceToEdit?.id ?: null, format: 'json']]" name='new-webconnection-form' method="post" onSuccess="checkForSuccessfulSave(data, i18n('webConnection.label') )"
		verticalTabs="webConnection.sorting,
				webConnection.configure,
				webConnection.confirm"
		templates="/webConnection/sorting,
				/webConnection/configure,
				/webConnection/confirm,
				/webConnection/save,
				/webConnection/validate"/>