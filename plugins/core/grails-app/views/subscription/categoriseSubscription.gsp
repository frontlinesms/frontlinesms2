<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="frontlinesms2.Subscription" %>
<meta name="layout" content="popup"/>
<div>
	<g:form name="categorize_subscription">
		<g:hiddenField id="messages" name="messagesList" value="${params.messageId}"/>
		<g:hiddenField id="owner" name="ownerId" value="${params.ownerId}"/>
		<p class="info">${Subscription.get(params.ownerId)?.name}</p>
		<a name='btn_join' class="btn" onclick="categoriseSubscription('join')" type="submit"><g:message code="subscription.join"/></a>
		<a name='btn_leave' class="btn" onclick="categoriseSubscription('leave')" type="submit"><g:message code="subscription.leave"/></a>
	</g:form>
</div>
<r:script>
function categoriseSubscription(option) {
	var messageList = $("#messages").val();
	var ownerId = $("#owner").val();
	var messageSection = $('input:hidden[name=messageSection]').val();
	if ((messageList.length) == 1) {
		var successUrl = "message/" + messageSection + "/" + ownerId + "/show/" + messagesToChange;
	} else {
		var successUrl = "message/" + messageSection + "/" + ownerId;
	}

	$.ajax({
		type:'POST',
		url: url_root + 'message/changeSubscriptionType',
		data: {subscriptionId: option , messageId: messageList, ownerId: ownerId},
		success: function(data) { window.location = url_root + successUrl; }
	});

}
</r:script>