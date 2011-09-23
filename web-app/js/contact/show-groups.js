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
	var groupList = $('.single-contact').is(':visible') ? $('#group-list') : $('#multi-group-list')
	var noGroup = $('.single-contact').is(':visible') ? $('#no-groups') : $('#multi-no-groups')

	var groupListItem = $('<li><input type="text" disabled="true" value="' + groupName + '" />');
	var deleteButton = $('<a class="remove-group" id="remove-group-' + groupId + '"><img src="' + url_root + 'images/icons/remove.png" /></a></li>');
	deleteButton.click(removeGroupClickAction);
	groupListItem.append(deleteButton);
	
	groupList.append(groupListItem);
	me.remove();
	noGroup.hide();
	addGroupId(groupId);

	// addIdToGroupHiddenField(groupId);
}

function removeGroupClickAction() {
	var me = $(this);
	var groupId = me.attr('id').substring('remove-group-'.length);
	var groupName = me.parent().children('input').val();
	
	var groupDropdown = $('.single-contact').is(':visible') ? $('#group-dropdown') : $('#multi-group-dropdown')
	var groupListElements = $('.single-contact').is(':visible') ? $('#group-list li input') : $('#multi-group-list input')
	var noGroup = $('.single-contact').is(':visible') ? $('#no-groups') : $('#multi-no-groups')
	var option = $("<option value='" + groupId + "'>" + groupName + '</option>');
	
	option.click(addGroupClickAction);
	groupDropdown.append(option);
	var groupList = me.parent();
	groupList.remove();
	if(groupListElements.size() <= 1) {
		noGroup.show();
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
