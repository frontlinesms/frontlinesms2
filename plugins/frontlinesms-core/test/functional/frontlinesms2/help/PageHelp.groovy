package frontlinesms2.help

import frontlinesms2.*
import frontlinesms2.popup.MediumPopup

class PageHelp extends MediumPopup {
	static at = { waitFor { popupTitle.contains("help") } }
	static content ={
		mainTitlesMessage{ $('') }
		subTitles { $('') }
		miniSubtitles { $('') }
	}
}