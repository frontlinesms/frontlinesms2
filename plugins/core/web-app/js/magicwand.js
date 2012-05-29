var magicwand = {
	wave: function(select, target) {
		var list = $('select[id^="'+select+'"]');
		var varName = list.val();
		insertAtCaret(target, "${" + varName + "}");
		magicwand.reset(list);
	},
	/**
	 * Reset a jquery "selectmenu" to display the original selected item.
	 * @arg list jquery selecter for the <option/> element
	 */
	reset: function(list) {
		list.selectmenu("destroy");
		list[0].selectedIndex = 0;
		magicwand.init(list);
	},

	init: function(list) {
		list.selectmenu();
		var status = list.parent().find(".ui-selectmenu-status");
		status.addClass("magicwand-button");
		status.removeClass("ui-selectmenu-status");
		status.text('');
		status.siblings(".ui-selectmenu-icon").remove();
		var statusParent = status.parent();
		statusParent.removeClass("ui-selectmenu");
	}
};

