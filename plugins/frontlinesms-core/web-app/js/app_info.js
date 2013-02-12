app_info = (function() {
	var
	requestData,
	callbacks,
	counter,
	init = function() {
		counter = 0;
		callbacks = [];
		requestData = [];
		timer.setInterval(callbackProcessor, 15000);
	},
	callbackProcessor = function(data) {
		$.getJSON(url_root + "appInfo", requestData, function(data) {
			var i, c;
			++counter;
			for(i=callbacks.length-1; i>=0; --i) {
				c = callbacks[i];
				console.log("app_info.callbackProcessor() :: c=" + JSON.stringify(c));
				if((counter % c.frequency) === 0) {
					c.callback(data);
				}
			}
		});
	},
	listen = function(interestedIn, fCallback, callback) {
		requestData.push(interestedIn);
		if(!callback) {
			callback = fCallback;
			fCallback = 1;
		}
		callbacks.push({ frequency:fCallback, callback:callback });
	};
	return { init:init, listen:listen };
}());

