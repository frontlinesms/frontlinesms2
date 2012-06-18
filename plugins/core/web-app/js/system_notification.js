var SystemNotification = function() {
	var _LIST_ACTION = url_root + "systemNotification/list";
	var _MARK_READ_ACTION = url_root + "systemNotification/markRead/";
	var _getId = function(e) {
		return e.attr("id").substring(13);
	};
	var _create = function(id, text) {
		var elementId = "notification-" + id;
		return '<div class="system-notification" id="' + elementId + '">'
				+ text
				+ '<a onclick="systemNotification.hide(' + id + ')" class="hider">x</a></div>';
	};
	var _hide = function(id) {
		$.get(_MARK_READ_ACTION + id);
		$("#notification-" + id).slideUp(500);
	};
	var _refresh = function() {
		$.get(_LIST_ACTION, function(data) {
			// remove any notifications no longer in the list
			var found = [];
			$(".system-notification").each(function(i, e) {
				e = $(e);
				var notificationId = _getId(e);
				if(!data[notificationId]) {
					// remove dead notification
					e.slideUp(500);
				} else {
					// prevent the notification being re-added
					data[notificationId] = null;
				}
			});

			// add any new notifications to the bottom of the list
			for(key in data) {
				var value = data[key];
				if(value) {
					$("#notifications").append(_create(key, value));
				}
			}
		});
	};
	return {
		refresh: _refresh,
		hide: _hide
	};
}

function editConnection(id){
	$.ajax({
  		url : url_root+"connection/wizard/"+id,
  		success : function(data){
  			launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false)
  		}
	});
}
