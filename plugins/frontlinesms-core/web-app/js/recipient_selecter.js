recipientSelecter = (function() {
	var recipientCount, fetchRecipientCount, getRecipientCount, updateRecipientCount, validateImmediate, validateDeferred;

	recipientCount = 0

	fetchRecipientCount = function() {
		var postData;
		postData = jQuery.param({recipients: jQuery('[name=recipients]').val()}, true);
		console.log("Recipient Count 0: " + recipientCount);
		jQuery.ajax({
			type: "POST",
			async: false,
			data: postData,
			url: url_root + "search/recipientCount",
			success: updateRecipientCount
		});
	};

	updateRecipientCount = function(data) {
		console.log("Recipient Count 1: " + recipientCount);
		recipientCount = data.recipientCount
		console.log("Recipient Count 2: " + recipientCount);
		$("#contacts-count").html(recipientCount);
		$("#messages-count").html(recipientCount);
	};

	/** Validate that at least one contact or mobile number is selected NOW! */
	validateImmediate = function() {
		fetchRecipientCount();
		return recipientCount > 0;
	};

	/**
	 * Validate that at least one contact, mobile number, group or smart group
	 * is selected, but allow empty groups.
	 */
	validateDeferred = function() {
		return $("[name=recipients]").val() || validateImmediate();
	};

	getRecipientCount = function() { fetchRecipientCount(); return recipientCount };

	return {
		validateImmediate:validateImmediate,
		validateDeferred:validateDeferred,
		fetchRecipientCount:fetchRecipientCount,
		getRecipientCount:getRecipientCount
	};
}());

