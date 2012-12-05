var recipientSelecter = (function() {
	var addAddressHandler, updateRecipientCount, searchForContacts, selectMembers, setContact, validateAddressEntry;

	function inCheckedGroup(value) {
		var checkedGroups, t;
		checkedGroups = $("li.group input:checked");
		t = false;
		$.each(checkedGroups, function(index, element) {
			if($.inArray(value, $.makeArray($(element).attr("groupMembers")))) {
				t = true;
			}
		});
		return t;
	}

	selectMembers = function(element, groupIdString, groupName, allContacts) {
		var mobileNumbers, contactMobileNumbers = {};
		$.each($("#mobileNumbers").val().split(","), function(index, value) { 
			if(value !== "" && !(value in contactMobileNumbers)) {
				contactMobileNumbers[value] = true;
			}
		});

		if($(element).attr("checked")) {
			$.each(allContacts, function(index, value) { 
				if(!(value in contactMobileNumbers)) {
					contactMobileNumbers[value] = true;
				}
			});
		} else {
			$.each(allContacts, function(index, value) {
				if(value in contactMobileNumbers) {
					if(!inCheckedGroup(value)) {
						delete contactMobileNumbers[value];
					}
				}
			});
		}

		mobileNumbers = $.map(contactMobileNumbers, function(index, value) {
			return value;
		});

		$("#mobileNumbers").val(($.makeArray(mobileNumbers).join(",")));

		updateRecipientCount();
	};

	setContact = function(element, contactNumber) {
		var mobileNumbers, contactMobileNumbers = {};
		$.each($("#mobileNumbers").val().split(","), function(index, value) { 
			if(!(value in contactMobileNumbers) && (value != "")) {
				contactMobileNumbers[value] = true;
			}
		});

		if($(element).attr("checked")) {
			if(!(contactNumber in contactMobileNumbers)) {
				contactMobileNumbers[contactNumber] = true;
			}
		} else {
			if(contactNumber in contactMobileNumbers) {
				if(! inCheckedGroup(contactNumber)) {
					delete contactMobileNumbers[contactNumber];
				}
			}
		}

		mobileNumbers = $.map(contactMobileNumbers, function(index, value) {
			return value;
		});

		$("#mobileNumbers").val(($.makeArray(mobileNumbers).join(",")));

		updateRecipientCount();
	}

	// FIXME current this method is unused
	function setValueForCheckBox(value, checked) {
		var checkBox = $("#contacts input[value='" + value + "']");
		checkBox.attr("checked", checked);
		checkBox.change();
	}

	updateRecipientCount = function() {
		var contactCount, mobileNumbersArray;
		if($("#mobileNumbers").val() != "") {
			mobileNumbersArray = $("#mobileNumbers").val().split(",");
		}
		contactCount = mobileNumbersArray? mobileNumbersArray.length:0 ;
		$("#contacts-count").html(contactCount);
		$("#messages-count").html(contactCount);
		$("#recipient-count").html(contactCount);
	};

	validateAddressEntry = function() {
		var address, containsLetters;
		address = $("#address").val();
		containsLetters = jQuery.grep(address, function(a) {
			return a.match(/[^\+?\d+]/) != null;
		}).join("");
		$("#address").removeClass("error");
		$("#manual-address").find("#address-error").remove();
		if(containsLetters != "" && containsLetters != null) {
			$("#address").addClass("error");
			$("#manual-address").append("<div id='address-error' class='error-message'><g:message code='fmessage.number.error'/></div>");
			return false;
		}
		return true;
	}

	addAddressHandler = function() {
		var address, checkbox, sanitizedAddress;
		address = $("#address").val();
		if(address === "") {
			return true;
		}
		sanitizedAddress = address.replace(/\D/g, "");
		if(address[0] === "+") {
			sanitizedAddress = "+" + sanitizedAddress;
		}
		checkbox = $("li.manual").find(":checkbox[value=" + sanitizedAddress + "]").val();
		if(checkbox !== address) {
			$("#contacts").prepend("<li class='manual contact' f-name='' f-number='" + sanitizedAddress + "'><input contacts='true' type='checkbox' onclick='recipientSelecter.setContact(this, \"" + sanitizedAddress + "\")' checked='true' name='addresses' value='" + sanitizedAddress + "'>" + sanitizedAddress + "</input></li>");
			$("li.manual.contact[f-number='"+sanitizedAddress+"'] input").trigger('click');
			$("li.manual.contact[f-number='"+sanitizedAddress+"'] input").attr('checked','checked');
			updateRecipientCount();
		}
		$("#address").val("");
		$("#address").removeClass("error");
		$("#manual-address").find("#address-error").remove();
		return true;
	};

	searchForContacts = function() {
		var search = $("#searchbox").val().toLowerCase();
		if(search === "") {
			$("li.contact").show();
			$("ul#groups").show();
			$(".matched-search-result").hide();
		} else {
			$("ul#groups").hide();
			$(".ui-tabs-panel #recipients-list ul#contacts li.contact").each(function () {
				if($(this).attr("f-name").toLowerCase().indexOf(search.toLowerCase()) == -1 
					&& $(this).attr("f-number").toLowerCase().indexOf(search.toLowerCase()) == -1) {
					$(this).hide();
					$(this).find(".matched-search-result").hide();
				} else {
					$(this).show();
					$(this).find(".matched-search-result").show();
				}
			});
		}
	}

	return {
		addAddressHandler:addAddressHandler,
		updateRecipientCount:updateRecipientCount,
		searchForContacts:searchForContacts,
		selectMembers:selectMembers,
		setContact:setContact,
		validateAddressEntry:validateAddressEntry
	};
}());

