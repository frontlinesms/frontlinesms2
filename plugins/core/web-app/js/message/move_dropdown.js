function moveAction() {
	var messageSection = $('input:hidden[name=messageSection]').val();
	var ownerId = $('input:hidden[name=ownerId]').val();
	var searchId = $("input:hidden[name=searchId]").val();
	var messagesToMove;
	var moveTarget;
	if(getCheckedItemCount('message') > 1) {
		// TODO should calculate selected IDs here rather than getting from hidden field.  Use
		// something like $('#message-list tr :checked');
		messagesToMove = getCheckedList('message');
		moveTarget = $('#multiple-messages select#move-actions option:selected');
	} else {
		messagesToMove = $("#message-id").val();
		moveTarget = $('#single-message select#move-actions option:selected');
	}

	var moveTargetType = moveTarget.attr("class").split(/\s+/)[0];
	if(moveTargetType == 'na') { return; }
	var moveTargetId = moveTarget.val();

	var location;
	if(moveTarget.hasClass('subscription')){
		launchCategorisePopup(moveTargetType,messagesToMove,moveTargetId);
		return;
	}else if(messageSection == 'result' && !(getCheckedItemCount('message') > 1)) {
		location = url_root + "search/" + messageSection + '/' + messagesToMove + '?searchId=' + searchId;
	} else if(messageSection == 'result') {
		location = url_root + "search/" + messageSection + '?searchId=' + searchId;
	} else if(messageSection == 'activity' || messageSection == 'folder' || messageSection == 'radioShow') {
		location = url_root + "message/" + messageSection + "/" + ownerId;
	} else {
		location = url_root + "message/" + messageSection;
	}
	// TODO no point in doing an AJAX call if we're going to move to a new page anyway - just
	// submit the form with HTTP POST like normal.
	$.ajax({
		type:'POST',
		url: url_root + 'message/move',
		data: { messageSection:moveTargetType, messageId:messagesToMove, ownerId:moveTargetId },
		success: function(data) { window.location = location; }
	});
}

function launchCategorisePopup(moveTargetType,messagesToMove,moveTargetId){
	$.ajax({
		type:'POST',
		url: url_root + 'subscription/categoriseSubscriptionPopup',
		data: { messageSection:moveTargetType, messageId:messagesToMove, ownerId:moveTargetId },
		success: function(data) { launchSmallPopup(i18n('subscription.categorise.title'), data, i18n('wizard.ok'), function() {
			var form = $("form#categorize_subscription");
			var action = form.find("input[type=radio]:checked").val();
			var action_url = url_root + "subscription/" + action;
			form.attr('action', action_url);
			form.submit();
		}) }
	});
}