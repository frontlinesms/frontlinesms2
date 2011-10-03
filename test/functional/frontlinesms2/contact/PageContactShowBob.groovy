package frontlinesms2.contact

import frontlinesms2.*

class PageContactShowBob extends geb.Page {
	static getUrl() { "contact/show/${Contact.findByName('Bob').id}" }
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		groupsList { $('#groups-submenu') }
		frmDetails { $("#contact_details") }
		btnSave { frmDetails.find('#update-single') }
		btnCancel { $(".buttons .cancel")}
	}
}
