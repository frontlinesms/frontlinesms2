var Ajax;
if (Ajax && (Ajax != null)) {
	Ajax.Responders.register({
	  onCreate: function() {
        if($('spinner') && Ajax.activeRequestCount>0)
          Effect.Appear('spinner',{duration:0.5,queue:'end'});
	  },
	  onComplete: function() {
        if($('spinner') && Ajax.activeRequestCount==0)
          Effect.Fade('spinner',{duration:0.5,queue:'end'});
	  }
	});
}

var remoteHash = {
	"poll" :  function() {
		$.ajax({
			type:'GET',
			dataType: "html",
			url: url_root + 'poll/create',
			success: function(data) {
				launchMediumWizard('Create Poll', data, 'Create', function(){initializePoll();});
			}})
	},

	"subscription": function() {
		$.ajax({
			type:'GET',
			dataType: "html",
			url: url_root + 'group/list',
			success: function(data) {
				launchMediumWizard('Manage Subscription', data, 'Create');
			}})
	},

	"quickMessage": function() {
		$.ajax({
			type:'GET',
			dataType: "html",
			url: url_root + 'quickMessage/create',
			success: function(data) {
				launchMediumWizard('Quick Message', data, 'Send', null, true);addTabValidations();
			}})
	},

	"announcement": function() {
		$.ajax({
			type:'GET',
			dataType: "html",
			url: url_root + 'quickMessage/create',
			success: function(data) {
				launchMediumWizard('Announcement', data, 'Send', null, true);addTabValidations();
			}})
	}
}

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

function moveToTabBy(index) {
	var tabWidget = $('#tabs').tabs();
	var selected = tabWidget.tabs('option', 'selected')
	tabWidget.tabs('select', selected + index);
	return false;
}

function moveToNextTab(canMoveToNextTab, onFailure, onSuccess) {
	onSuccess = onSuccess || null
	if (canMoveToNextTab) {
		if (onSuccess != null)
			onSuccess()
		else
			moveToTabBy(1);
	}
	else
		onFailure()
	return false

}

$('.next').live('click', function() {
	if($(this).hasClass('disabled')) return;
	return moveToNextTab(true);
});

$('.back').live('click', function() {
	return moveToTabBy(-1);
});

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


$.widget("ui.TabContentWidget", {
	validate: function() {
		return this.options['validate'].call();			
	},
	options: {validate: function() {return true;}}
});