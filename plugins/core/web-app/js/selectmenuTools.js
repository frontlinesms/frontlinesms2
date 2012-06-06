var selectmenuTools = {
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
		menu.find("[value='"+$('#'+selectId).val()+"']").remove();
		selectmenuTools.snapback(menu);
	},
}