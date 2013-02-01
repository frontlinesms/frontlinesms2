<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<fsms:wizard url="${[action:'save', controller:'subscription', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]}" name="create_subscription" method="post" onSuccess="checkForSuccessfulSave(data, i18n('subscription.label'))"
		verticalTabs="radioShow.selectShow,
				subscription.group.header,
				subscription.sorting,
				subscription.autoreplies,
				subscription.confirm"
		templates="/radioShow/selectShow,
				/subscription/group,
				/subscription/sorting,
				/subscription/autoreplies,
				/subscription/confirm,
				/subscription/save,
				/subscription/validate"/>

