$(document).ready(function() {
	initContactPaneGroups();
});

function initContactPaneGroups() {
	$("#group-list li a.remove-command").click(removeGroupClickAction);
	$("#group-dropdown").change(addGroupClickAction);
	$("#multi-group-dropdown").change(addGroupClickAction);
	$("#multi-group-list li a.remove-command").click(removeGroupClickAction);
}

function addGroupClickAction() {
	var me, groupName, groupId, groupList, noGroup, groupListItem, deleteButton;
	me = $(this).find('option:selected');
	if(me.hasClass('not-group')) { return; }
	groupName = me.text();
	groupId = me.attr('value');
	groupList = $('.single-contact').is(':visible') ? $('#group-list') : $('#multi-group-list');
	noGroup = $('.single-contact').is(':visible') ? $('#no-groups') : $('#multi-no-groups');

	groupListItem = $('<li class="" groupName="' + groupName + '"><span>' + groupName + '</span>');
	deleteButton = $('<a class="remove-command icon-remove" id="remove-group-' + groupId + '"></a></li>');
	deleteButton.click(removeGroupClickAction);
	groupListItem.append(deleteButton);
	
	groupList.append(groupListItem);
	me.remove();
	noGroup.hide();
	addGroupId(groupId);

	var contactEditForm = $(".contact-edit-form");
	contactEditForm.trigger("addedGroupToContact");
}

function removeGroupClickAction() {
	var me, groupId, groupName, groupDropdown, groupListElements, noGroup, option, groupList;
	me = $(this);
	groupId = me.attr('id').substring('remove-group-'.length);
	groupName = me.parent().children('span').text();

	groupDropdown = $('.single-contact').is(':visible') ? $('#group-dropdown') : $('#multi-group-dropdown');
	groupListElements = $('.single-contact').is(':visible') ? $('#group-list li span') : $('#multi-group-list ');
	noGroup = $('.single-contact').is(':visible') ? $('#no-groups') : $('#multi-no-groups');
	option = $("<option value='" + groupId + "'>" + groupName + '</option>');
	
	option.click(addGroupClickAction);
	groupDropdown.append(option);
	groupList = me.parent();
	groupList.remove();
	if(groupListElements.size() <= 1) {
		noGroup.show();
	}
	removeGroupId(groupId);
	selectmenuTools.refresh(groupDropdown);
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
	var f, oldList, newList;
	f = $('input:hidden[name=' + fieldName + ']');
	oldList = f.val();
	newList = oldList.replace(','+ id +',', ',');
	f.val(newList);
}
function addIdToList(id, fieldName) {
	var f, oldList, newList;
	f = $('input:hidden[name=' + fieldName + ']');
	oldList = f.val();
	newList = oldList + id + ',';
	f.val(newList);
}
