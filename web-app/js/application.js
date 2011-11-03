$(function() {
	$('#tab-messages').everyTime(refresh_rate || '30s', "refreshCountTimer", refreshMessageCount);
});

function refreshMessageCount() {
	$.get(url_root + 'message/getUnreadMessageCount', function(data) {
		$('#tab-messages').html("Messages " + data);
	});
}

// Please DO NOT add to this. Functions should be in their own file with a relevant name so that they can easily found
// FIXME move these functions somewhere more useful
var remoteHash = {
	"export": function() {
		$.ajax({
			type:'GET',
			url: url_root + 'export/wizard',
			data: {messageSection: $("#messageSection").val(), ownerId: $('#ownerId').val(), activityId: $("#activityId").val(),
					searchString: $("#searchString").val(), groupId: $("#groupId").val(), messageTotal: $("#messageTotal").val(),
					failed: $("#failed").val(), starred: $("#starred").val(), viewingArchive: $("#viewingArchive").val()},
			success: function(data) {
				launchSmallPopup('Export', data, 'Export');
				updateExportInfo();
			}})
	},

	"renameActivity": function() {
		$.ajax({
			type:'GET',
			url: url_root + 'poll/rename',
			data: {ownerId: $("#ownerId").val()},
			success: function(data) {
				launchSmallPopup('Rename activity', data, 'Rename');
			}})
	},
	
	"deleteAction": function() {
		var messageSection = $("#messageSection").val();
		$.ajax({
			type:'GET',
			url: url_root + messageSection + '/confirmDelete',
			data: {id: $("#ownerId").val()},
			success: function(data) {
				launchSmallPopup('Delete ' + messageSection, data, 'Delete');
			}})
	},
}

$(function() {
	$("#poll-actions, #folder-actions").bind('change', function() {
		var selected = $(this).find('option:selected').val();
		if(selected)
			remoteHash[selected].call();
	});
});

$("#export").click(function() {
	remoteHash['export'].call();
});

function isElementEmpty(selector) {
	return isEmpty($(selector).val());
}

function isEmpty(val) {
	return val.trim().length == 0
}

function isGroupChecked(groupName) {
	return getSelectedGroupElements(groupName).length > 0;
}

function getSelectedGroupElements(groupName) {
	return $('input[name=' + groupName + ']:checked');
}

function isDropDownSelected(id) {
	var selectedOptions = $("#" + id + " option:selected")
	return selectedOptions.length > 0  && (!isEmpty(selectedOptions[0].value))
}

$('.check-bound-text-area').live('focus', function() {
  	var checkBoxId = $(this).attr('checkbox_id');
	$('#' + checkBoxId).attr('checked', true);
});

function findInputWithValue(value) {
	return $('input[value=' + "'" + value + "'" + ']');
}

function isCheckboxSelected(value) {
	return findInputWithValue(value).is(':checked')
}

$.fn.renderDefaultText = function() {
	return this.focus( function() {
		$(this).toggleClass('default-text-input', false);
		var element = $(this).val();
		$(this).val( element === this.defaultValue ? '' : element );
	}).blur(function(){
		var element = $(this).val();
		$(this).val( element.match(/^\s+$|^$/) ? this.defaultValue : element );
		$(this).toggleClass('default-text-input', $(this).val() === this.defaultValue);
		});
};
