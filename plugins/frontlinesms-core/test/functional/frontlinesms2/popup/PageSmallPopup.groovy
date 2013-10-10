package frontlinesms2.popup

import frontlinesms2.*

abstract class SmallPopup extends geb.Page {
	static content = {
		popupTitle {
			$('#ui-dialog-title-modalBox').text()?.toLowerCase()
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
		popupTitle == 'smallpopup.customfield.create.title'
	}
	static content = {
		newField(required:false) { $('#modalBox #custom-field-popup #custom-field-name') }
	}
}

class GroupPopup extends SmallPopup {
	static at = {
		popupTitle == 'smallpopup.group.create.title'
	}
	static content = {
		groupName { $('#modalBox #group-details #name') }
	}
}

class RenameGroupPopup extends SmallPopup {
	static at = {
		popupTitle == 'smallpopup.group.rename.title'
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
		popupTitle == 'smallpopup.group.delete.title'
	}
	static content = {
		warningMessage { $('#modalBox div.dialog p').text()?.toLowerCase() }
	}
}

class DeleteActivity extends SmallPopup {
	static at = {
		popupTitle.contains('delete')
	}
	static content = {
		text { $('#confirmDelete h2') }
	}
}

class CreateFolderPopup extends SmallPopup {
	static at = {
		popupTitle == 'smallpopup.folder.create.title'
	}
	static content = {
		errorPanel { $('#modalBox #smallpopup-error-panel') }
		folderName { $('#modalBox #folder-details #name') }
	}
}

class RenameFolderDialog extends SmallPopup {
	static at = {
		popupTitle == 'smallpopup.folder.rename.title'
	}
	static content = {
		errorPanel { $('#modalBox #smallpopup-error-panel') }
		folderName { $('#modalBox #folder-details #name') }
	}
}

class RenameActivityDialog extends SmallPopup {
	static at = {
		popupTitle == 'smallpopup.activity.rename.title'
	}
	static content = {
		errorPanel { $('#modalBox #smallpopup-error-panel') }
		activityName { $('#modalBox #name') }
	}
}

class RenameSubscriptionDialog extends SmallPopup {
	static at = {
		popupTitle == 'smallpopup.subscription.rename.title'
	}
	static content = {
		errorPanel { $('#modalBox #smallpopup-error-panel') }
		subscriptionName { $('#modalBox #name') }
	}
}

class DeleteFolderPopup extends SmallPopup {
	static at = {
		popupTitle == 'smallpopup.folder.delete.title'
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
		popupTitle.contains('smallpopup.test.message.title')
	}
	static content = {
		addresses { $('input#addresses').text() }
		message { $('textarea#messageText').text() }
	}
}

class SubscriptionCategoriseDialog extends SmallPopup {
	static at = {
		popupTitle.contains('categorise messages')
	}
	static content = {
		groupName { $('input#group-dropdown') }
		join { $('#modalBox input#join') }
		leave { $('#modalBox input#leave') }
		toggle { $('#modalBox input#toggle') }
	}
}
