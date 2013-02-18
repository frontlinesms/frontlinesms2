var SystemNotification = function() {
	var
	_MARK_READ_ACTION = url_root + "systemNotification/markRead/",
	_getId = function(e) {
		return e.attr("id").substring(13);
	},
	_create = function(id, text) {
		var elementId = "notification-" + id;
		return '<div class="system-notification" id="' + elementId + '">'
				+ text
				+ '<a onclick="systemNotification.hide(' + id + ')" class="hider">x</a></div>';
	},
	hide = function(id) {
		$.get(_MARK_READ_ACTION + id);
		$("#notification-" + id).slideUp(500);
	},
	_refresh = function(data) {
		// remove any notifications no longer in the list
		var _key, value, found;
		if(!data.system_notification) {
			return;
		}
		data = data.system_notification;
		found = [];
		$(".system-notification").each(function(i, e) {
			e = $(e);
			var notificationId = _getId(e);
			if(!data[notificationId]) {
				// remove dead notification
				e.slideUp(200);
			} else {
				// prevent the notification being re-added
				data[notificationId] = null;
			}
		});

		// add any new notifications to the bottom of the list
		for(_key in data) {
			value = data[_key];
			if(value) {
				$("#notifications").append(_create(_key, value));
			}
		}
	},
	init = function() {
		app_info.listen("system_notification", _refresh);
	};
	return {
		hide:hide,
		init:init
	};
};

