ajax_spy = (function() {
	var
	requests,
	responses,
	defaultResponse,
	init = function(_defaultResponse) {
		requests = [],
		responses = [],
		defaultResponse = _defaultResponse || function() {
			return {};
		};
		$.ajax = processAjax;
		$.getJSON = processGetJson;
	},
	addResponse = function(request, response) {
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

		// TODO map args to jQuery.ajax args
		// TODO attempt to match request to response
		responseData = responseData || processResponse(defaultResponse);
		requests.push({ url:settings.url, data:requestData, response:responseData });
		settings.success(responseData);
	},
	processGetJson = function(url, data, callback) {
		var responseData;
		// TODO map args to jQuery.ajax args
		// TODO attempt to match request to response
		responseData = responseData || processResponse(defaultResponse);
		requests.push({ url:url, data:data, response:responseData });
		callback(responseData);
	},
	processResponse = function(r) {
		if(typeof r === "function") {
			return r();
		}
		if(typeof r === "string") {
			return JSON.stringify(r);
		}
		return r;
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

