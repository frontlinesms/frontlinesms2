dom_trix = (function() {
	var jsdom, loadModule,
			initDomFromString, initDomFromFile, initEmptyDom, initJquery,
			initJqueryValidation, initI18n, jqueryValidations;

	loadModule = function(moduleName) {
		var mod;
		try {
			console.log("Loading module: " + moduleName + "...");
			mod = require(moduleName);
			console.log(moduleName + " loaded.");
			return mod;
		} catch(err) {
			console.log("Error trying to load module: " + moduleName + ": " + err);
			console.error("ERROR: " + err);
			process.exit(1);
		}
	}
	jsdom = loadModule("jsdom");

	initEmptyDom = function() {
		initDomFromString("<html><head></head><body></body></html");
	};

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
		var fs = loadModule('fs');
		initDomFromString(fs.readFileSync(file).toString());
	};

	initI18n = function() {
		console.log("Initialising i18n...");
		global.i18n = function() {};
		console.log("i18n initialiased.");
	};

	initJquery = function() {
		var jq;
		console.log("Initialising jQuery...");
		global.jQuery = window.jQuery = global.$ = window.$ = loadModule("jquery").create(global.window);
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
		initEmptyDom:initEmptyDom,
		initDomFromString:initDomFromString,
		initDomFromFile:initDomFromFile
	};
}());


