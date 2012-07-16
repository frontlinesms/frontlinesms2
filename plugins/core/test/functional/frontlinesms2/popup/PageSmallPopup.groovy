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
		newField { $('#modalBox #custom-field-popup #custom-field-name') }
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

class DeleteGroupPopup extends SmallPopup {
	static at = {
		popupTitle.contains("delete group")
	}
	static content = {
		warningMessage { $('#modalBox div.dialog p').text().toLowerCase() }
	}
}