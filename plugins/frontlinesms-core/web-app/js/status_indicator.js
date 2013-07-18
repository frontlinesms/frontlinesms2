var StatusIndicator = function() {
	var _failureCount = 0,
	_updateLight = function(color) {
		var statusIndicator = $('#status-indicator');
		statusIndicator.removeClass('green');
		statusIndicator.removeClass('red');
		statusIndicator.addClass(color);
		statusIndicator.show();
	},
	_getConnectionLostNotification = function() {
		return $("#server-connection-lost-notification");
	},
	_errorHandler = function() {
		if(_failureCount === 0) {
			++_failureCount;
		} else if(!_getConnectionLostNotification().length) {
			var notification = '<div id="server-connection-lost-notification"><div class="content"><p>'
					+ i18n('server.connection.fail.title')
					+ '</p><p>'
					+ i18n('server.connection.fail.info')
					+ '</p></div></div>';
			$('body').append($(notification));
		}
		_updateLight('red');
	},
	_successHandler = function(data) {
		var connectionLostNotification;
		data = data.status_indicator;
		if(!data) { return; }
		connectionLostNotification = _getConnectionLostNotification();
		_failureCount = 0;
		if(connectionLostNotification) {
			connectionLostNotification.remove();
		}
		_updateLight(data);
	},
	init = function() {
		app_info.listen("status_indicator", _successHandler);
		app_info.listenForFailures(_errorHandler);
	};

	return { init:init };
};

