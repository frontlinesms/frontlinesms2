app_info = (function() {
	var
	callbacks,
	counter,
	init = function() {
		counter = 0;
		callbacks = [];
		timer.setInterval(requester, 15000);
	},
	callbackProcessor = function(data) {
		var i, c;
		for(i=callbacks.length-1; i>=0; --i) {
			c = callbacks[i];
			if((counter % c.frequency) === 0) {
				c.callback(data);
			}
		}
	},
	requester = function() {
		var requestData = [], i, c;
		++counter;
		for(i=callbacks.length-1; i>=0; --i) {
			c = callbacks[i];
			if((counter % c.frequency) === 0) {
				requestData.push(c.interest);
			}
		}
		$.getJSON(url_root + "appInfo", requestData, callbackProcessor);
	},
	listen = function(interestedIn, fCallback, callback) {
		if(!callback) {
			callback = fCallback;
			fCallback = 1;
		}
		for(i=callbacks.length-1; i>=0; --i) {
			if(callbacks[i].interest === interestedIn) {
				throw "Should not have two listeners for the same data!";
			}
		}
		callbacks.push({ interest:interestedIn, frequency:fCallback, callback:callback });
	};
	return { init:init, listen:listen };
}());

