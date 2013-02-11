app_info = (function() {
	var
	requestData = [],
	callbacks = {},
	counter = 0,
	callbackProcessor = function(data) {
		$.getJSON(url_root + "appInfo", requestData, function(data) {
			var i, c;
			++counter;
			for(i=callbacks.length-1; i>=0; --i) {
				c = callbacks[i];
				if((c.frequency % counter) === 0) {
					c.callback(data);
				}
			}
		});
	},
	listen = function(interestedIn, fCallback, callback) {
		requestData.push(interestedIn);
		if(!callback) {
			fCallback = 1;
			callback = fCallback;
		}
		callbacks.push({ frequency:fCallback, callback:callback });
	};

	init = function() {
		setInterval(callbackProcessor, 15000);
	};
	return { init:init, listen:listen };
}());

