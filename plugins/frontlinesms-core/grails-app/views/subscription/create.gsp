<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<fsms:wizard url="${[action:'save', controller:'subscription', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]}" name="create_subscription" method="post" onSuccess="checkForSuccessfulSave(data, i18n('subscription.label'))"
		verticalTabs="subscription.group.header,
				subscription.sorting,
				subscription.autoreplies,
				subscription.confirm"
		templates="/subscription/group,
				/subscription/sorting,
				/subscription/autoreplies,
				/subscription/confirm,
				/subscription/save,
				/subscription/validate"/>
