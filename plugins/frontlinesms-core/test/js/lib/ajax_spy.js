ajax_spy = (function() {
	var
	addResponse = function(request, response) {
		responses.push({ rq:request, res:response });
	},
	defaultResponse,
	init = function(_defaultResponse) {
		defaultResponse = _defaultResponse || function() {
			return {};
		};
		$.ajax = processAjax;
		$.getJSON = processAjax;
	},
	processAjax = function(url, data, callback) {
		var responseData;
		requests.push({ url:url, data:data, callback:callback });
		// TODO map args to jQuery.ajax args
		// TODO attempt to match request to response
		responseData = processResponse(defaultResponse);
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
	requests = [],
	responses = [],
	requestCount = function() {
		return responses.length;
	},
	___end___;
	return {
		addResponse:addResponse,
		init:init,
		requestCount:requestCount
	};
}());

