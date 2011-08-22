$(document).ready(function() {
	$("#group-list li a.remove-group").click(removeGroupClickAction);
	$("#group-dropdown").change(addGroupClickAction);
	$("#multi-group-dropdown").change(addGroupClickAction);
	$("#multi-group-list li a.remove-group").click(removeGroupClickAction);
});

function addGroupClickAction() {
	var me = $(this).find('option:selected');
	if(me.hasClass('not-group')) return;
	var groupName = me.text();
	var groupId = me.attr('value');

	var groupListItem = $('<li><input type="text" readonly="readonly" value="' + groupName + '" />');
	var deleteButton = $('<a class="remove-group" id="remove-group-' + groupId + '">Delete</a></li>');
	deleteButton.click(removeGroupClickAction);
	groupListItem.append(deleteButton);
	
	if($('.single-contact').is(':visible')) {
		$('#group-list').append(groupListItem);
		me.remove();
		$("#no-groups").hide();
		addGroupId(groupId);
		
	} else {
		$('#multi-group-list').append(groupListItem);
		me.remove();
		$("#multi-no-groups").hide();
		addGroupId(groupId);
	}
	// addIdToGroupHiddenField(groupId);
}

function removeGroupClickAction() {
	var me = $(this);
	var groupId = me.attr('id').substring('remove-group-'.length);
	var groupName = me.parent().children('input').val();

	var option = $("<option value='" + groupId + "'>" + groupName + '</option>');
	option.click(addGroupClickAction);
	
	if($('.single-contact').is(':visible')) {
		$('#group-dropdown').append(option);
		var groupList = me.parent();
		groupList.remove();
		if($('#group-list li').children('input').length < 1) {
			$('#no-groups').show();
		}
		
	} else {
		$('#multi-group-dropdown').append(option);
		var groupList = me.parent();
		groupList.remove();
		if($('#multi-group-list li').children('input').length < 1) {
			$('#multi-no-groups').show();
		}
	}
	
	

	removeGroupId(groupId);
	// removeIdFromGroupHiddenField(groupId);
}

function removeGroupId(id) {
	// remove from the ADD list
	removeIdFromList(id, 'groupsToAdd');
	// add to the REMOVE list
	addIdToList(id, 'groupsToRemove');
}
function addGroupId(id) {
	// remove from the REMOVE list
	removeIdFromList(id, 'groupsToRemove');
	// add to the ADD list
	addIdToList(id, 'groupsToAdd');
}
function removeIdFromList(id, fieldName) {
	var f = $('input:hidden[name=' + fieldName + ']');
	var oldList = f.val();
	var newList = oldList.replace(','+ id +',', ',');
	f.val(newList);
}
function addIdToList(id, fieldName) {
	var f = $('input:hidden[name=' + fieldName + ']');
	var oldList = f.val();
	var newList = oldList + id + ',';
	f.val(newList);
}
