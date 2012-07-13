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
		println "123123" + popupTitle;
		popupTitle.contains("delete")
	}
	static content = {
		text { $('#confirmDelete h2').text() }
	}
}