var selectmenuTools = {
	initAll: function(selecter) {
		var elements = $(selecter);
		elements.selectmenu();
		elements.each(function(i, e) {
			e = $(e).next().find(".ui-selectmenu-status");
			if(!e.text()) e.html("&nbsp;");
		});
	},

	init: function(menu) {
		menu = $(menu);
		menu.selectmenu();
	},

	refresh: function(menu) {
		menu = $(menu);
		menu.selectmenu("destroy");
		menu.selectmenu();
	},

	snapback: function(menu) {
		menu = $(menu);
		menu.selectmenu("destroy");
		menu[0].selectedIndex = 0;
		selectmenuTools.init(menu);
	},

	removeSelected: function(menu) {
		menu = $(menu);
		menu.find("[value='" + menu.val() + "']").remove();
		selectmenuTools.snapback(menu);
	}
};

