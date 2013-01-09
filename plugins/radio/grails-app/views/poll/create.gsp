<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<fsms:wizard url="[action: 'save', controller:'poll', params: [ownerId:activityInstanceToEdit?.id ?: null, format: 'json']]" name='new-poll-form' method="post" onSuccess="checkForSuccessfulSave(data, i18n('poll.label') )"
		verticalTabs="radioShow.selectShow,
				poll.question,
				poll.response,
				poll.sort,
				poll.reply,
				poll.edit.message,
				poll.recipients,
				poll.confirm"
		templates="/radioShow/selectShow,
				/poll/question,
				/poll/responses,
				/poll/aliases,
				/poll/replies,
				/message/compose,
				/message/select_recipients,
				/poll/confirm,
				/poll/save,
				/poll/validate"/>