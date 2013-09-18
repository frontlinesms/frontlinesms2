ajax_spy = (function() {
	var
	requests,
	responses,
	defaultResponse,
	log = function(message, methodName) {
		var root = "# ajax_spy";
		if(typeof methodName !== "undefined") {
			root += "." + methodName + "()";
		}
		console.log(root + " :: " + message);
	},
	init = function(_defaultResponse) {
		requests = [];
		responses = [];
		defaultResponse = _defaultResponse || function() {
			return {};
		};
		$.ajax = processAjax;
		$.getJSON = processGetJson;
	},
	assertType = function(obj, types) {
		var t = typeof obj;
		if(types.indexOf(t) === -1) {
			throw "Bad type: " + t;
		}
	},
	addResponse = function(request, response) {
		assertType(request, ["string", "function"]);
		assertType(response, ["string", "function"]);
		responses.push({ rq:request, res:response });
	},
	processAjax = function(url, settings) {
		var requestData, responseData;
		if(typeof url !== "string") {
			settings = url;
		} else {
			settings.url = url;
		}

		requestData = settings.data;
		if(settings.processData === false && settings.contentType === "application/json") {
			requestData = JSON.parse(requestData);
		}

		try {
			responseData = processRequest(url, requestData);
			requests.push({ url:settings.url, data:requestData, response:responseData });
			settings.success(responseData);
		} catch(ex) {
			settings.error(null, null, ex);
		}
	},
	processGetJson = function(url, data, callback) {
		var responseData;
		// TODO map args to jQuery.ajax args
		// TODO attempt to match request to response
		responseData = processRequest(url, data);
		requests.push({ url:url, data:data, response:responseData });
		callback(responseData);
	},
	processRequest = function(url, data) {
		var r, t;
		for(i=0; i<responses.length; ++i) {
			r = responses[i];
			t = typeof r.rq;
			if((t === "string" && r.rq === url) ||
					(t === "function" && r.rq(url, data))) {
				return triggerResponder(r.res, url, data);
			}
		}
		return triggerResponder(defaultResponse, url, data);
	},
	triggerResponder = function(responder, url, data) {
		if(typeof responder === "function") {
			return responder(url, data);
		}
		if(typeof responder === "string") {
			return JSON.stringify(responder);
		}
		return responder;
	},
	requestCount = function() {
		return requests.length;
	},
	___end___;
	return {
		addResponse:addResponse,
		init:init,
		requestCount:requestCount,
		getRequest:function(i) { return requests[i]; },
		getRequests:function() { return requests; }
	};
}());

