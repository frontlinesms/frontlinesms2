package frontlinesms2.connection

import frontlinesms2.popup.MediumPopup

class ConnectionDialog extends MediumPopup {
	static at = {
		popupTitle.contains('connection') || popupTitle.contains("edit")
	}
	static content = {
		connectionType { $("input[name=connectionType]") }
		connectionForm { $('#connectionForm') }
		confirmName { $("#confirm-name") }
		confirmType { $("#confirm-type") }
		confirmPort { $("#confirm-port") }

		basicInfo { connectionType ->
			$("input", name:'connectionType', value:connectionType).parent().find('p.info').text()
		}

		confirmIntelliSmsConnectionName { $("#intellisms-confirm #confirm-name") }
		confirmIntelliSmsUserName { $("#intellisms-confirm #confirm-username") }
		confirmIntelliSmsType { $("#intellisms-confirm #confirm-type") }

		confirmSmssyncName { $('#smssync-confirm #confirm-name') }
		confirmSmssyncSecret { $('#smssync-confirm #confirm-secret') }
		confirmSmssyncReceiveEnabled { $('#smssync-confirm #confirm-receiveEnabled') }
		confirmSmssyncSendEnabled { $('#smssync-confirm #confirm-sendEnabled') }

		error { $('label', class:'error') }
	}
}

