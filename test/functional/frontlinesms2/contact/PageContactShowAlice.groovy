package frontlinesms2.contact

import frontlinesms2.*


class PageContactShowAlice extends geb.Page {
	static getUrl() { "contact/show/${Contact.findByName('Alice').id}" }
	static content = {
		contactSelect(required:false) { $(".contact-select") }
		deleteSingleButton(required:false) { $('#btn_delete') }
		deleteAllButton(required:false) { $('#btn_delete_all') }
		confirmDeleteButton(required:false) { $("#done") }
		flashMessage(required:false) { $('div.flash') }
		frmDetails { $("#contact_details") }
		btnSave { frmDetails.find('#update-single') }
		btnCancel { $(".buttons .cancel")}
	}
}