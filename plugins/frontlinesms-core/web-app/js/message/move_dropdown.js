function moveAction() {
	var location, messageSection, messagesToMove, moveTarget, moveTargetId, moveTargetType, ownerId, searchId;
	messageSection = $('input:hidden[name=messageSection]').val();
	ownerId = $('input:hidden[name=ownerId]').val();
	searchId = $("input:hidden[name=searchId]").val();
	if(getCheckedItemCount('interaction') > 1) {
		// TODO should calculate selected IDs here rather than getting from hidden field.  Use
		// something like $('#message-list tr :checked');
		messagesToMove = getCheckedList('interaction');
		moveTarget = $('#multiple-interactions select#move-actions option:selected');
	} else {
		messagesToMove = $("#interaction-id").val();
		moveTarget = $('#single-interaction select#move-actions option:selected');
	}

	moveTargetType = moveTarget.attr("class").split(/\s+/)[0];
	if(moveTargetType === 'na') { return; }
	moveTargetId = moveTarget.val();

	// FIXME should not have activity-specific code in common javascript
	if(moveTarget.hasClass('subscription')) {
		launchCategorisePopup(moveTargetType, messagesToMove, moveTargetId);
		return;
	}

	if(messageSection === 'result' && getCheckedItemCount('interaction') === 0) {
		location = url_root + "search/" + messageSection + '/' + messagesToMove + '?searchId=' + searchId;
	} else if(messageSection === 'result') {
		location = url_root + "search/" + messageSection + '?searchId=' + searchId;
	} else if(messageSection === 'activity' || messageSection === 'folder' || messageSection === 'radioShow') {
		// FIXME should not have radio-specific code in core application
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
		beforeSend : function() { showThinking(); },
		url: url_root + 'subscription/categoriseSubscriptionPopup',
		data: { messageSection:moveTargetType, messageId:messagesToMove, ownerId:moveTargetId },
		success: function(data) {
			hideThinking();
			launchSmallPopup(i18n('subscription.categorise.title'), data, i18n('wizard.ok'), function() {
				var action, action_url, form;
				form = $("form#categorize_subscription");
				action = form.find("input[type=radio]:checked").val();
				action_url = url_root + "subscription/" + action;
				form.attr('action', action_url);
				form.submit();
			});
		}
	});
}

