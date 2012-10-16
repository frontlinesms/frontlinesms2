var selectmenuTools = {
	isSupported: function() {
		/* Currently the layout for selectmenus does not work
			in certain browsers.  These should be detected
			for here */
		if($.browser.msie && $.browser.version <= 7) return false;
		// more conditions here
		else return true;
	},

	initAll: function(selecter, parent) {
		if(!selectmenuTools.isSupported()) return;
		var elements = parent? parent.find(selecter): $(selecter);
		elements.selectmenu();
		elements.each(function(i, e) {
			e = $(e).next().find(".ui-selectmenu-status");
			if(!e.text()) e.html("&nbsp;");
		});
	},

	init: function(menu) {
		if(!selectmenuTools.isSupported()) return;
		menu = $(menu);
		menu.selectmenu();
	},

	refresh: function(menu) {
		if(!selectmenuTools.isSupported()) return;
		menu = $(menu);
		menu.selectmenu("destroy");
		menu.selectmenu();
	},

	snapback: function(menu) {
		if(!selectmenuTools.isSupported()) return;
		menu = $(menu);
		menu.selectmenu("destroy");
		menu[0].selectedIndex = 0;
		selectmenuTools.init(menu);
	},

	removeSelected: function(menu) {
		if(!selectmenuTools.isSupported()) return;
		menu = $(menu);
		menu.find("[value='" + menu.val() + "']").remove();
		selectmenuTools.snapback(menu);
	}
};

