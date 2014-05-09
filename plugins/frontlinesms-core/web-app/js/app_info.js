app_info = (function() {
	var
	callbacks,
	counter,
	fibonacciTimeOffsetPair = [1, 1],
	failCallback,
	repeatIntervalOffset,
	init = function(initialRepeatIntervalOffset) {
		counter = 0;
		callbacks = {};
		repeatIntervalOffset = initialRepeatIntervalOffset || 15000;
		setTimeout(requester, repeatIntervalGenerator());
	},
	callbackProcessor = function(data) {
		var c;
		for(c in callbacks) {
			if(callbacks.hasOwnProperty(c)) {
				c = callbacks[c];
				if((counter % c.frequency) === 0) {
					// TODO should check that requested data is present and only
					// do callback if that is the case.  N.B. difference between
					// null and undefined is v important in this case
					c.callback(data);
				}
			}
		}
	},
	failureProcessor = function(data) {
		if(typeof failCallback !== "undefined") {
			failCallback.call();
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
			//FIXME Should fix type to GET but POST is set to prevent rendering of blank pages
			jQuery.ajax({ type:"POST",
				url:url_root + "appInfo",
				cache:false,
				contentType:"application/json",
				data:JSON.stringify(requestData),
				processData:false,
				success:callbackProcessor,
				error:failureProcessor });
		}
		setTimeout(requester, repeatIntervalGenerator());
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
	listenForFailures = function(listener) {
		if(typeof listener !== "function") {
			throw "Failure listener must be a function.";
		}
		if(typeof failCallback !== "undefined") {
			throw "Cannot override old failure listener.";
		}
		failCallback = listener;
	},
	repeatIntervalGenerator = function() {
		fibonacciTimeOffsetPair = [
			fibonacciTimeOffsetPair[1],
			fibonacciTimeOffsetPair[0] + fibonacciTimeOffsetPair[1]
		];
		var nextRepeatInterval = fibonacciTimeOffsetPair[0] * 1000 + repeatIntervalOffset;
		return nextRepeatInterval;
	},
	stopListening = function(disinterest) {
		delete callbacks[disinterest];
	};

	return { init:init, listen:listen, listenForFailures:listenForFailures, stopListening:stopListening };
}());

