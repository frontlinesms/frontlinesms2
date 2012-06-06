var FsmsButton = function() {
	var
		_trigger = function() {
			// Trigger clicking of the button when the anchor is clicked.
			$(this).prev().click();
		},
		_apply = function(original) {
			// replace a button with an anchor
			// find the original text
			original = $(original);
			if(original.hasClass("fsms-button-replaced")) { alert("Already replaced :)"); return; }
			original.addClass("fsms-button-replaced");
			var buttonText = original.val() || original.text();
			// create the new control
			var newController = $("<a>" + buttonText + "</a>");

			var displayNone = original.css("display") == "none";
			if(displayNone) newController.css("display", "none");
			var copyAttributes = ["class", "disabled"];
			for(i=copyAttributes.length-1; i>=0; --i) {
				var a=copyAttributes[i];
				newController.attr(a, original.attr(a));
			}
			newController.click(_trigger);
			
			// add the new control next to original
			original.after(newController);
			// hide the current control
			original.hide();
		},
		_findAndApply = function(selecter, parent) {
			var found;
			if(parent) found = parent.find(selecter);
			else found = $(selecter);
			found.each(function(i, e) { _apply(e); });
		};
	return { apply:_apply, trigger:_trigger, findAndApply:_findAndApply };
};

