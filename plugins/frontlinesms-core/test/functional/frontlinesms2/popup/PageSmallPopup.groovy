package frontlinesms2.popup

import frontlinesms2.*

abstract class SmallPopup extends geb.Page {
	static content = {
		popupTitle {
			$('#ui-dialog-title-modalBox').text().toLowerCase()
		}
		cancel { $('button#cancel') }
		ok { $('button#done') }
	}
}

class DeletePopup extends SmallPopup {
	static at = {
		popupTitle.contains("delete")
	}
	static content = {
		text { $('#confirmDelete h2').text() }
	}
}

class CustomFieldPopup extends SmallPopup {
	static at = {
		popupTitle.contains("create custom field")
	}
	static content = {
		newField(required:false) { $('#modalBox #custom-field-popup #custom-field-name') }
	}
}

class GroupPopup extends SmallPopup {
	static at = {
		popupTitle.contains("group")
	}
	static content = {
		groupName { $('#modalBox #group-details #name') }
	}
}

class RenameGroupPopup extends SmallPopup {
	static at = {
		popupTitle.contains("rename group")
	}
	static content = {
		groupName { $('#modalBox #name') }
	}
}

class RenameSmartGroupPopup extends RenameGroupPopup {
	static content = {
		smartGroupName { $('input#smartgroupname') }
	}
}

class DeleteGroupPopup extends SmallPopup {
	static at = {
		popupTitle.contains("delete group")
	}
	static content = {
		warningMessage { $('#modalBox div.dialog p').text().toLowerCase() }
	}
}

class DeleteActivity extends SmallPopup {
	static at = {
		popupTitle.contains("delete activity")
	}
	static content = {
		text { $('#confirmDelete h2') }
	}
}

class CreateFolderPopup extends SmallPopup {
	static at = {
		popupTitle.contains("folder")
	}
	static content = {
		errorPanel { $('#modalBox #smallpopup-error-panel') }
		folderName { $('#modalBox #folder-details #name') }
	}
}

class RenameFolderDialog extends SmallPopup {
	static at = {
		popupTitle.contains("rename folder")
	}
	static content = {
		errorPanel { $('#modalBox #smallpopup-error-panel') }
		folderName { $('#modalBox #folder-details #name') }
	}
}

class RenameSubscriptionDialog extends SmallPopup {
	static at = {
		popupTitle.contains("activity")
	}
	static content = {
		errorPanel { $('#modalBox #smallpopup-error-panel') }
		subscriptionName { $('#modalBox #name') }
	}
}

class DeleteFolderPopup extends SmallPopup {
	static at = {
		popupTitle.contains("delete folder")
	}
	static content = {
		text { $('#modalBox #confirmDelete h2') }
	}
}

class EmptyTrashPopup extends SmallPopup {
	static at = {
		popupTitle.contains("empty trash?")
	}
	static content = {
		text { $('#confirmEmptyTrash p') }
	}
}

class TestMessagePopup extends SmallPopup {
	static at ={
		popupTitle.contains('test message')
	}
	static content = {
		addresses { $('input#addresses').text() }
		message { $('textarea#messageText').text() }
	}
}

class SubscriptionCategoriseDialog extends SmallPopup {
	static at = {
		popupTitle.contains('categorise subscription')
	}
	static content = {
		groupName { $('input#group-dropdown') }
		join { $('#modalBox input#subscription-action').value("join") }
		leave { $('#modalBox input#subscription-action').value("leave") }
		toggle { $('#modalBox input#subscription-action').value("toggle") }
	}
}