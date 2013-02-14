app_info = (function() {
	var
	callbacks,
	counter,
	init = function() {
		counter = 0;
		callbacks = {};
		timer.setInterval(requester, 1000);
	},
	callbackProcessor = function(data) {
		var c;
		for(c in callbacks) {
			if(callbacks.hasOwnProperty(c)) {
				c = callbacks[c];
				if((counter % c.frequency) === 0) {
					c.callback(data);
				}
			}
		}
	},
	requester = function() {
		var requestData = {}, interest, c, data;
		++counter;
		for(c in callbacks) {
			if(callbacks.hasOwnProperty(c)) {
				interest = c;
				c = callbacks[c];
				if((counter % c.frequency) === 0) {
					data = c.data || null;
					if(typeof data === "function") {
						data = data.call();
					}
					requestData[interest] = data;
				}
			}
		}
		if(!jQuery.isEmptyObject(requestData)) {
			console.log("requester() :: requestData=" + JSON.stringify(requestData));
			jQuery.ajax({ type:"POST",
				url:url_root + "appInfo",
				contentType:"application/json",
				data:JSON.stringify(requestData),
				processData:false,
				success:callbackProcessor });
		}
	},
	listen = function(interest, f, data, callback) {
		if(!interest || !f) {
			throw "Must indicate interest and callback.";
		}
		if(!callback && !data) {
			callback = f;
			f = 1;
		} else if(!callback) {
			callback = data;
			if(typeof f === "number") {
				data = null;
			} else {
				data = f;
				f = 1;
			}
		}
		if(callbacks.hasOwnProperty(interest)) {
			throw "Should not have two listeners for the same data!";
		}
		callbacks[interest] = { frequency:f, callback:callback, data:data };
	},
	stopListening = function(disinterest) {
		delete callbacks[disinterest];
	};

	return { init:init, listen:listen, stopListening:stopListening };
}());

