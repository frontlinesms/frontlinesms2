var magicwand = {
	wave: function(select, target) {
		var list, varName;
		list = $('#' + select);
		varName = list.val();
		if(varName !== "na") {
			insertAtCaret(target, "${" + varName + "}");
		}
		magicwand.reset(list);
		$("#"+target).trigger("keyup");
	},
	/**
	 * Reset a jquery "selectmenu" to display the original selected item.
	 * @arg list jquery selecter for the <option/> element
	 */
	reset: function(list) {
		if(magicwand.isSupported()) {
			list.selectmenu("destroy");
		}
		list[0].selectedIndex = 0;
		magicwand.init(list);
	},

	isSupported: function() {
		return selectmenuTools.isSupported() &&
				!(jQuery.browser.msie && jQuery.browser.version <= 8);
	},

	init: function(list) {
		var status, statusParent;
		if(!magicwand.isSupported()) { return; }
		list.selectmenu();
		status = list.parent().find(".ui-selectmenu-status");
		status.text('');
		status.prepend('<span class="magicwand-icon"/>');
		status.attr('title', i18n("magicwand.title"));
		status.siblings(".ui-selectmenu-icon").remove();
		status.removeClass("ui-selectmenu-status");
		status.addClass("btn");
		statusParent = status.parent();
		statusParent.removeClass("ui-selectmenu");
		statusParent.css("display", "block");
	}
};

