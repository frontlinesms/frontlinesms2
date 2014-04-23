var inline_editable;
$(function() {
	inline_editable = new InlineEditable();
});
InlineEditable = function() {
	var
	updateInProgress = false,
	init = function() {
		$('.inline-editable').blur(handleBlur);	
	},
	handleBlur = function() {
		var getDataAttr = function(name, selector) {
			return $(selector).attr('data-' + name);
		},
		newValue = $(this).val(),
		dataToSend = {};
		$.each(['domainclass', 'field', 'instanceid', 'savedvalue'], function(index, fieldName) {
			dataToSend[fieldName] = getDataAttr(fieldName, event.target);
		});
		if(dataToSend.savedvalue === newValue) {
			return;
		}
		dataToSend.value = newValue;
		$.ajax({
			type:"POST",
			url:url_root + "inlineEditable/update",
			data:dataToSend,
			beforeSend:function() {
				setUpdateInProgress(true, event.target);
				toggleFormEnabled(false, event.target);
			},
			success:handleServerResponse
		});
	},
	toggleFormEnabled = function(enabled, targetElement) {
		if (enabled) {
			$("input.inline-editable").removeAttr('disabled');
		}
		else {
			targetElement = $(targetElement);
			$("input.inline-editable").not(targetElement).attr('disabled','disabled');
		}
	},

	setUpdateInProgress = function(inProgress, targetElement) {
		targetElement = $(targetElement);
		targetElement.addClass('in-progress');
		updateInProgress = inProgress;
		if(updateInProgress) {
			targetElement.after("<i class='update in-progress'/>");
		} else {
			$(".update.in-progress").removeClass('in-progress').addClass('fa fa-check').addClass('done').fadeOut(1000, function() { $(this).remove(); });
			$('label.server-side-error').remove();
		}
	}
	handleServerResponse = function(data) {
		var targetElement = $('input.inline-editable.in-progress');
		if(data.success) {
			toggleFormEnabled(true);
			setUpdateInProgress(false);
			targetElement.removeClass('in-progress').attr('data-savedvalue', targetElement.val());
		}
		else {
			$('label.server-side-error').remove();
			$(".update.in-progress").removeClass('in-progress');
			targetElement.parent().append("<label class='server-side-error'>"+ data.error +"</label>");
		}
	},
	resizeToFit = function(selector) {
		$(selector).attr('size', Math.max($(this).val().length + 5, 20));
	};

	init();
};
