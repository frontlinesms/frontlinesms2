dom_trix = (function() {
	var jsdom;
	try {
		console.log("Loading JSDOM...");
		jsdom = require('jsdom');
		console.log("JSDOM loaded.");
	} catch(err) {
		console.log("Error trying to load JSDOM: " + err);
		process.exit(1);
	}

	var initDomFromString, initDomFromFile, initJquery, initJqueryValidation, initI18n, jqueryValidations;

	initDomFromString = function(domString) {
		console.log("Initialising DOM with HTML: " + domString + "...");
		global.document = jsdom.jsdom(domString, null, { features: { QuerySelector: true } });
		global.window = global.document.createWindow();
		global.navigator = { userAgent:'node-js' };
		initJquery();
		initJqueryValidation();
		initI18n();
		console.log("DOM initialiased.");
	};

	initDomFromFile = function(file) {
		var fs = require('fs');
		initDomFromString(fs.readFileSync(file).toString());
	};

	initI18n = function() {
		console.log("Initialising i18n...");
		global.i18n = function() {};
		console.log("i18n initialiased.");
	};

	initJquery = function() {
		console.log("Initialising jQuery...");
		global.jQuery = window.jQuery = global.$ = window.$ = require("jquery").create(global.window);
		console.log("jQuery initialiased.");
	};

	initJqueryValidation = function() {
		console.log("Initialising jQuery validation...");
		jqueryValidations = { elements:[], errors:[] };
		global.validator = {
			element:function(e) { jqueryValidations.elements.push(e); },
			showErrors:function(e) { jqueryValidations.errors.push(e); }
		};
		console.log("jQuery validation initialiased.");
	};

	return {
		initDomFromString:initDomFromString,
		initDomFromFile:initDomFromFile
	};
}());


