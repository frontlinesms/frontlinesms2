var groupMembershipEditor;
$(function() {
	groupMembershipEditor = new GroupMembershipEditor();
	groupMembershipEditor.init();
});

var GroupMembershipEditor = function() {
	var toAdd = [], toRemove = [];
	this.init = function() {
		$("#group-list li a.remove-command").click(removeGroupClickAction);
		$("#group-dropdown").change(addGroupClickAction);
		$("#multi-group-dropdown").change(addGroupClickAction);
		$("#multi-group-list li a.remove-command").click(removeGroupClickAction);
	}

	function isSingleContactEdit() {
		return $('.single-contact').is(':visible');
	}

	function addGroupClickAction() {
		var me, groupName, groupId, groupList, groupListItem;
		me = $(this).find('option:selected');
		if(me.hasClass('not-group')) { return; }
		groupName = me.text();
		groupId = me.attr('value');
		groupList = isSingleContactEdit()? $('#group-list'): $('#multi-group-list');

		groupListItem = $(sanchez.template("group-membership", {id:groupId, name:groupName}));
		groupListItem.find(".remove-command").click(removeGroupClickAction);
		
		groupList.append(groupListItem);
		me.remove();
		getNoGroupMessage().hide();
		addGroupId(groupId);
	}

	function getNoGroupMessage() {
		return isSingleContactEdit()? $('#no-groups'): $('#multi-no-groups');
	}

	function isNoGroupsSelected() {
		var groupListElements = isSingleContactEdit()? $('#group-list li'): $('#multi-group-list');
		return groupListElements.size() <= 1;
	}

	function removeGroupClickAction(event) {
		var me, groupId, groupName, groupDropdown, option;
		me = $(this);
		groupId = me.attr('id').substring('remove-group-'.length);
		groupName = me.parent().children('span').text();

		groupDropdown = isSingleContactEdit()? $('#group-dropdown'): $('#multi-group-dropdown');
		option = $("<option value='" + groupId + "'>" + groupName + '</option>');
		
		option.click(addGroupClickAction);
		groupDropdown.append(option);
		me.parent().remove();
		if(isNoGroupsSelected()) {
			getNoGroupMessage().show();
		}
		removeGroupId(groupId);
		selectmenuTools.refresh(groupDropdown);

		if(isSingleContactEdit()) { contactEditor.updateContactData(event); }
	}
	function removeGroupId(id) {
		toAdd.remove(id);
		toRemove.push(id);
		updateFormLists();
	}
	function addGroupId(id) {
		toRemove.remove(id);
		toAdd.push(id);
		updateFormLists();
		//TODO Quick fix for a bug Firefox ... Please forgive me
		if(isSingleContactEdit()) { 
			var event = { target : $("#group-dropdown") }
			contactEditor.updateContactData(event);
		}
	}
	function updateFormLists() {
		$('input:hidden[name=groupsToAdd]').val(toAdd.join(","));
		$('input:hidden[name=groupsToRemove]').val(toRemove.join(","));
	}
};

