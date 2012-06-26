package frontlinesms2.popup

import frontlinesms2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

abstract class MediumPopup extends geb.Page {
	static at = {
		$('div.ui-dialog').displayed
	}
	static content = {
		cancel { $('button#cancel') }
		next { $('button#nextPage') }
		previous { $('button#prev') }
		send { $('button#submit') }
		tab { tabId -> 
			$('#tabs a[href="#tabs-'+tabId+'"]')
		}
		errorPanel { $('div.error-panel') }
		error { errorPanel.text().toLowerCase() }
	}
}

class QuickMessageDialog extends MediumPopup {
	static content = {
		compose { module QuickMessageComposeTab }
		recipients { module QuickMessageRecipientsTab }
		confirm { module QuickMessageConfirmTab }
	}
}

class QuickMessageComposeTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		textArea { $('textarea#messageText') }
	}
}

class QuickMessageRecipientsTab extends geb.Module {
	static base = { $('div#tabs-2') }
	static content = {
		
	}
}

class QuickMessageConfirmTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		recipientName { $('td#recipient').text() }
	}
}

class CreateActivityDialog extends MediumPopup {
	static base = { $('div.ui-dialog') }
	static content = {
		poll { $('input[value="poll"]') }
		announcement { $('input[value="announcement"]') }
		autoreply { $('input[value="autoreply"]') }
	}
}

class PollDialog extends MediumPopup {
	static content = {
		compose { module PollComposeTab }
		response { module PollResponseTab }
		sort { module PollSortTab }
		autoreply { module PollAutoReplyTab }
		edit { module PollEditMessageTab }
		recipients { module PollRecipientsTab }
		confirm { module PollConfirmTab }
		summary { module PollSummary }
	}
}

class PollComposeTab extends geb.Module {
	static base = { $('div#tabs-1') }
	static content = {
		yesNo { $('div.input input[value="yesNo"]') }
		multiple { $('div.input input[value="multiple"]') }
		question { $('textarea#question') }
		dontSendQuestion { $('input#dontSendMessage') }
	}
}

class PollResponseTab extends geb.Module {
	static base = { $('div#tabs-2') }
	static content = {
		choice { choiceLetter -> 
			$('input#choice${choiceLetter}"]')
		}
	}
}

class PollSortTab extends geb.Module {
	static base = { $('div#tabs-3') }
	static content = {
		dontSort { $('ul.select input[value="false"]') }
		sort { $('ul.select input[value="true"]') }
		keyword { $('input#poll-keyword') }
	}
}

class PollAutoReplyTab extends geb.Module {
	static base = { $('div#tabs-4') }
	static content = {
		autoreplyCheck { $('input#enableAutoreply') }
		text { $('textarea#autoreplyText') }
		keyword { $('input#poll-keyword') }
	}
}

class PollEditMessageTab extends geb.Module {
	static base = { $('div#tabs-5') }
	static content = {
		autoreplyCheck { $('textarea#messageText') }
	}
}

class PollRecipientsTab extends geb.Module {
	static base = { $('div#tabs-6') }
	static content = {
		addNumber { $('input#address') }
	}
}

class PollConfirmTab extends geb.Module {
	static base = { $('div#tabs-7') }
	static content = {
		pollName { $('input#name') }
	}
}

class PollSummary extends geb.Module {
	static base = { $('div.sumamry') }
	static content = {

	}
}