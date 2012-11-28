var newFeatures = (function() {
	var closePopup, showPopup, _doPopupDisplay, _handlePopupClose, showGuider, _doShowGuider;
	_doPopupDisplay = function(data) {
		var modalBox;
		mediumPopup.launchNewFeaturePopup(
				i18n("newfeatures.popup.title"),
				data,
				i18n("action.close"),
				closePopup);
		modalBox = $("#modalBox");
		modalBox.addClass("help");
		modalBox.wrapInner("<div id='new-feature-content' class='help-content'/>");
		buttonSet = $(".ui-dialog-buttonset");
		buttonSet.prepend("<label><input type='checkbox' name='enableNewFeaturesPopup' id='enableNewFeaturesPopup' checked='checked'/>" +
				i18n("newfeatures.popup.showinfuture") +
				"</label>");
		buttonSet.attr("id", "new-feature-buttonset");
	};
	showPopup = function() {
		jQuery.ajax({
			url:url_root + "help/newfeatures",
			success:_doPopupDisplay });
	};
	_handlePopupClose = function(data) {
		var modalBox = $("#modalBox");
		modalBox.parent().remove();
	};
	closePopup = function() {
		var showAgain = $("#enableNewFeaturesPopup").is(":checked");
		$.ajax({
			url: url_root + "help/updateShowNewFeatures",
			data:{ enableNewFeaturesPopup:showAgain },
			cache: false,
			success: _handlePopupClose
		});
	};
	showGuider = function(name) {
		jQuery.ajax({
			url:url_root + "help/guider",
			data:{ name:name },
			dataType:'json',
			success:_doShowGuider,
			error:function() { alert(i18n("guider.error.notfound", name)); } });
	};
	_doShowGuider = function(data) {
		alert("Should now show guider using data: " + JSON.stringify(data));
	};
	return {
		showPopup:showPopup,
		closePopup:closePopup,
		showGuider:showGuider
	};
}());

