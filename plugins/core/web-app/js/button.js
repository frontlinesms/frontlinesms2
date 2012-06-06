var fsmsButton = {
	trigger: function() {
		// Trigger clicking of the button when the anchor is clicked.
		$(this).prev().click();
	},
	apply: function(original) {
		// replace a button with an anchor
		// find the original text
		original = $(original);
		if(original.hasClass("fsms-button-replaced")) return;
		original.addClass("fsms-button-replaced");
		var buttonText = original.val() || original.text();

		// create the new control
		var newController = $("<a>" + buttonText + "</a>");

		var displayNone = original.css("display") == "none";
		if(displayNone) newController.css("display", "none");

		var copyAttributes = ["class", "disabled"];
		for(i in copyAttributes) {
			var a=copyAttributes[i];
			newController.attr(a, original.attr(a));
		}
		newController.click(fsmsButton.trigger);

		// add the new control next to original
		original.after(newController);

		// hide the current control
		original.hide();
	},
	findAndApply: function(selecter, parent) {
		var found;
		if(parent) found = parent.find(selecter);
		else found = $(selecter);
		found.each(function() { fsmsButton.apply(this); });
	}
};

