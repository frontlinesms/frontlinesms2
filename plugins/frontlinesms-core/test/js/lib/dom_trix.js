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

	var initDomFromString, initDomFromFile, initJquery, resetDom;
	initDomFromString = function(domString) {
		console.log("Initialising DOM with HTML: " + domString + "...");
		var document = jsdom.jsdom(domString, null, { features: { QuerySelector: true } }),
			window = document.createWindow(),
			navigator = {
				userAgent: 'node-js'
			};
		global.window = window;
		global.navigator = navigator;
		global.document = window.document;
		console.log("DOM SETUP COMPLETE");
		initJquery();
	};
	initDomFromFile = function(file) {
		var fs = require('fs');
		initDomFromString(fs.readFileSync(file).toString());
	};
	initJquery = function() {
		console.log("Initialising jQuery...");
		var jQuery = require("jquery");
		global.jQuery = jQuery;
		global.$ = jQuery;
		console.log("jQuery initialiased.");
	};
	resetDom = function() {
		// TODO
	};
	return {
		initDomFromString:initDomFromString,
		initDomFromFile:initDomFromFile,
		resetDom:resetDom
	};
}());


