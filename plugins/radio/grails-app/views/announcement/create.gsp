<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<fsms:wizard url="${[action:'save', controller:'announcement', params:[ownerId:activityInstanceToEdit?.id ?: null, format:'json']]}" name="create_announcement" method="post" onSuccess="checkForSuccessfulSave(data, i18n('announcement.label'))"
		verticalTabs="radioShow.selectShow,
				announcement.create.message,
				announcement.select.recipients,
				announcement.confirm"
		templates="/radioShow/selectShow,
				/message/compose,
				/message/select_recipients,
				/announcement/confirm,
				/announcement/save,
				/announcement/validate"/>


