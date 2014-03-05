var contactEditor;
$(function() {
	contactEditor = new ContactEditor();
});

var ContactEditor = function() {
	var cachedFormHash,
	fieldsToAdd = [], fieldsToRemove = [],
	contactEditForm = $(".contact-edit-form"),
	savingStateMessage = $(".edit .saving-state-message"),
	contactEditWrapper = contactEditForm.find(".edit"),
	field = function(name) {
		var selecter = $.map(arguments, function(v, i) {
			return "[name=" + v + "]"; }).join();
		return contactEditForm.find(selecter); },
	updateInProgress, updateRequested,
	updateContactData = function(event) {
		var formData = contactEditForm.serialize(),
		formHashAtRequestTime = formData.hashCode(),
		targetElement = $(event.target),
		dataToSend = targetElement
				.add(field("contactId", "fieldsToAdd", "fieldsToRemove", "groupsToAdd", "groupsToRemove"));
		if(targetElement.attr("name") === "name") {
			dataToSend = dataToSend.add(field("mobile"));
		} else if(targetElement.attr("name") === "mobile") {
			dataToSend = dataToSend.add(field("name"));
		}
		dataToSend = dataToSend.serialize();
		if(formHashAtRequestTime !== cachedFormHash) {
			$.ajax({
				type:"POST",
				url:url_root + "contact/saveContact",
				data:dataToSend,
				beforeSend:function() {
					if(updateInProgress) {
						updateRequested = true;
						return false;
					}
					setUpdateInProgress(true, event.target);
					setSavingStateMessage("contact.status.saving");
					disableForm(event.target);
				},
				complete:function() {
					setUpdateInProgress(false, event.target);
					if(updateRequested) {
						updateRequested = false;
						updateContactData(event);
					}
				},
				success:function(data) {
					cachedFormHash = formHashAtRequestTime;
					if(data.success) {
						handleSuccessResponse(event, data);
					} else {
						handleFailureResponse(event, data);
					}
					reenableFormElements(data.success);
				}
			});
		} else {
			resetMobileField();
		}
	},
	handleSuccessResponse = function(event, data) {
		var contactName, mainListContactLink, mainListContactLinkKids, button, buttonKids,
			flagElement, mobileField, nonPrettyPhoneNumber,
			targetElement = $(event.target);

		contactEditWrapper.removeClass("has-server-errors");
		contactEditWrapper.removeClass("submit-in-progress");
		targetElement.removeClass("server-side-error");
		if(targetElement.attr('name') === 'name' ||
				targetElement.attr('name') === 'mobile') {
			$("label.server-side-error[for=mobile], label.server-side-error[for=name]").remove();
			field("name", "mobile").removeClass('server-side-error');
		}

		contactName = field("name").val();
		mainListContactLink = $('#main-list-container li.selected a');
		mainListContactNumberPreview = mainListContactLink.children();
		mainListContactLink.text(contactName);
		if(data.contactPrettyPhoneNumber) {
			mainListContactNumberPreview.text(data.contactPrettyPhoneNumber);
		} else {
			mainListContactNumberPreview.text("-");
		}
		mainListContactLink.append(mainListContactNumberPreview);

		flagElement = $('.flag');
		flagElement.removeClass().addClass(data.flagCSSClasses);

		mobileField = $("#mobile");
		nonPrettyPhoneNumber = mobileField.val();
		mobileField.attr("data-nonPrettyPhoneNumber", nonPrettyPhoneNumber);
		mobileField.attr("data-prettyPhoneNumber", data.contactPrettyPhoneNumber);
		mobileField.val(data.contactPrettyPhoneNumber);
		validateMobile();
		$(".warning.NonNumericNotAllowedWarning").hide("fast");

		button = $('#single-contact a.send-message');
		buttonKids = button.children();
		button.text(" " + i18n("contact.send.message", contactName));
		button.prepend(buttonKids);
		showSuccessfullySavedMessage();
		updateLastSavedValue();
	},
	handleFailureResponse = function(event, data) {
		var
			targetElement = $(event.target),
			localFieldName = event.target.name,
			errors = data.errors[localFieldName];
		targetElement.addClass("server-side-error");
		$.each(errors, function(index, item) {
			targetElement.parent().append("<label class='server-side-error' for='"+ localFieldName +"'>"+ item +"</label>");
		});
		contactEditWrapper.addClass("has-server-errors");
		setSavingStateMessage("contact.status.unsaved");
	},
	disableForm = function(targetElement) {
		targetElement = $(targetElement);
		targetElement.parent().find("label.server-side-error").remove();
		contactEditWrapper.addClass("submit-in-progress");
		contactEditWrapper.find("textarea,input[type='text']").not("#contact-search").not(targetElement).attr('disabled','disabled');
		selectmenuTools.disable("#new-field-dropdown");
		selectmenuTools.disable("#group-dropdown");
	},
	dismissWarning = function(warningType) {
		$.ajax({
			type:"GET",
			url:url_root + "contact/disableWarning",
			data: { 'warning': warningType },
			complete: function() {
				$(".warning."+warningType).fadeOut(300, function() { $(this).remove(); } );
			}
		});
		$(this).find("i").removeClass("in-progress").addClass("icon-loading");

	},
	reenableFormElements = function(removeErrorMessages) {
		contactEditWrapper.find("textarea,input[type='text']").removeAttr("disabled");
		$("#new-field-dropdown").attr("disabled", false).selectmenu();
		$("#group-dropdown").attr("disabled", false).selectmenu();
		selectmenuTools.enable("#new-field-dropdown");
		selectmenuTools.enable("#group-dropdown");
		if(removeErrorMessages) {
			$("label.server-side-error").remove();
		}
	},
	setUpdateInProgress = function(inProgress, targetElement) {
		targetElement = $(targetElement);
		updateInProgress = inProgress;
		if(updateInProgress) {
			targetElement.after("<i class='update in-progress'/>");
		} else {
			targetElement.parent().find(".update.in-progress").removeClass('in-progress').addClass('icon-ok').addClass('done').fadeOut(1000, function() { $(this).remove(); });
		}
	},
	removeCustomFieldClickHandler = function(event) {
		var fieldId, fieldElement, fieldName;
		fieldId = $(this).attr('id').substring('remove-field-'.length);
		fieldElement = $(this).parent().parent();
		fieldName = fieldElement.find('label').text();
		fieldElement.remove();
		$("#new-field-dropdown option[value='na']").after('<option value="'+fieldName+'">'+fieldName+'</option>');
		selectmenuTools.refresh($('#new-field-dropdown'));
		fieldsToAdd.remove(fieldName);
		fieldsToRemove.push(fieldName);
		updateHiddenFieldsForAddAndRemove();
		updateContactData(event);
	};

	function removeNonNumericCharacters() {
		var mobileField = $(this), prettyPhoneNumber, nonPrettyPhoneNumber;
		prettyPhoneNumber = mobileField.val();
		nonPrettyPhoneNumber = prettyPhoneNumber.replace(/[^0-9\+]/g,"");
		mobileField.val(nonPrettyPhoneNumber);
	}

	function resetMobileField() {
		var mobileField, mobileFieldNonPrettyPhoneNumber, mobileFieldPrettyPhoneNumber;
		mobileField = field("mobile");
		mobileFieldNonPrettyPhoneNumber = mobileField.attr("data-nonPrettyPhoneNumber");
		mobileFieldPrettyPhoneNumber = mobileField.attr("data-prettyPhoneNumber");
		mobileField.val(mobileFieldPrettyPhoneNumber);
	}

	function validateMobile() {
		var val, sendMessageButton, notInternationalFormat,
		mobileField = $('input[name=mobile]');
		val = mobileField.val().trim();

		if(fsms_config["mobileNumbers.international.warn"] && !fsms_settings["international.number.format.warning.disabled"]) {
			notInternationalFormat = val && !(/^\+/.test(val)) && (/[0-9]+/).test(val);
			$(".warning.l10nWarning").showIf(notInternationalFormat, "fast");
			if(!notInternationalFormat) {
				mobileField.removeClass("error");
			}
		}

		$("#contact-infos a.send-message").toggleClass("hidden", val === "");
	}

	function setSavingStateMessage(messageCode, args) {
		savingStateMessage.html(i18n(messageCode, args));
	}

	function showUnsavedChangesMessage() {
		var newFormHash = contactEditForm.serialize().hashCode();
		if(newFormHash !== cachedFormHash) {
			setSavingStateMessage("contact.status.unsaved");
		}
	}

	function showSuccessfullySavedMessage() {
		var saveDate = new Date();
		setSavingStateMessage("contact.status.saved", ("0" + saveDate.getHours()).slice(-2)   + ":" + 
				    ("0" + saveDate.getMinutes()).slice(-2));
	}

	function checkMobileNumberForNonNumericCharacters() {
		var nonNumericCharactersFound;
		if(!fsms_config["mobileNumbers.nonNumeric.warn"] || fsms_settings["non.numeric.characters.removed.warning.disabled"]) { return; }
		nonNumericCharactersFound = !(/^\+?[0-9]*$/.test($(this).val().trim()));
		$(".warning.NonNumericNotAllowedWarning")
				.showIf(nonNumericCharactersFound, "fast");
	}

	function escapeHanlder() {
		if(typeof(event) !== 'undefined' && event.keyCode == 27) {
			var element = $(this);
			var lastSavedValue = element.attr("lastSavedValue");
			element.val(lastSavedValue);
			element.trigger("blur");
			savingStateMessage.html("");
		}
		return true;
	}

	function updateLastSavedValue() {
		$.each($(".edit input[type=text], .edit textarea"), function(index, element) {
			var element = $(element);
			element.attr("lastsavedvalue", element.val());
		});
	}

//> CUSTOM FIELD STUFF START
	this.checkCustomFieldResult = function(json) {
		var name, i;
		if ($("#custom-field-name").val() !== "") {
			name = $("#custom-field-name").val();
			for(i=0; i<fieldsToAdd.length; ++i) {
				if(fieldsToAdd[i] !== "") { json.uniqueCustomFields.push(fieldsToAdd[i]); }
			}
			for(i=0; i<json.uniqueCustomFields.length; ++i) {
				if (json.uniqueCustomFields[i].toLowerCase() === name.toLowerCase()) {
					$("#smallpopup-error-panel")
							.html(i18n("customfield.validation.error"))
							.show();
					return false;
				}
			}
			addCustomField(name);
			$("#modalBox").remove();
		} else {
			$("#smallpopup-error-panel")
					.html(i18n("customfield.validation.prompt"))
					.show();
		}
	};

	function addFieldClickAction() {
		var me, fieldName;
		me = $(this).find('option:selected');
		if(me.hasClass('not-field')) { return; }
		if(me.hasClass('create-custom-field')) {
			$.ajax({
				type:'POST',
				url:url_root + 'contact/newCustomField',
				beforeSend:showThinking,
				success:function(data, textStatus) {
					hideThinking();
					launchSmallPopup(i18n("smallpopup.customfield.create.title"), data, i18n("action.ok"), clickDone);
				}
			});
		} else {
			fieldName = me.text();
			addCustomField(fieldName);
			me.remove();
		}
		selectmenuTools.snapback(this);
	}

	function clickDone() {
		if ($("#modalBox").contentWidget("onDone")) {
			$(this).find("form").submit();
		}
	}

	function addCustomField(name) {
		fieldsToRemove.remove(name);
		fieldsToAdd.push(name);
		updateHiddenFieldsForAddAndRemove();
		$("#info-add").parent().before(sanchez.template("custom-field-input", {name:name, fieldName:"newCustomField-"+name, removerName:""}));
		$(".contact-edit-form").trigger("addedCustomFieldToContact");
	}

	function updateHiddenFieldsForAddAndRemove() {
		$("input:hidden[name=fieldsToAdd]").val(fieldsToAdd.join(","));
		$("input:hidden[name=fieldsToRemove]").val(fieldsToRemove.join(","));
	}
//> CUSTOM FIELD STUFF END

//> MASS GROUP EDIT START
	function enableSaveButton() {
		var element, selecters;

		selecters = ["#update-all"];
		for(i=selecters.length-1; i>=0; --i) {
			element = $(selecters[i]);
			element.removeAttr("disabled");
			element.removeClass("disabled");
			if(element.hasClass("fsms-button-replaced")) {
				element.next().removeClass("disabled");
			}
		}
	}

//> INITIALISE
	function init() {
		cachedFormHash = contactEditForm.serialize().hashCode();
		$("#notes").autosize();

		$(".edit input[type=text], .edit textarea").keyup(escapeHanlder);
		$("input[name=mobile]")
				.focus(removeNonNumericCharacters)
				.keyup(validateMobile)
				.keyup(checkMobileNumberForNonNumericCharacters)
				.keyup(showUnsavedChangesMessage);
		validateMobile();

		// bind form data change listeners
		$(".edit input[type=text], .edit textarea, .edit input[type=hidden], .edit select:not(#group-dropdown)")
				.change(updateContactData)
				.keyup(showUnsavedChangesMessage);

		contactEditForm.bind("addedCustomFieldToContact", function() {
			$(".edit input[type=text]")
				.blur(updateContactData)
				.keyup(showUnsavedChangesMessage);
			$(".edit .custom-field .remove-command").click(removeCustomFieldClickHandler);
			$(".edit input[type=text], .edit textarea").keyup(escapeHanlder);
		});
		$(".edit .custom-field .remove-command").click(removeCustomFieldClickHandler);

		$("#new-field-dropdown").change(addFieldClickAction);

		$("#multi-group-list .remove-group").click(enableSaveButton);
		$("#multi-group-dropdown").change(enableSaveButton);
	}

	this.updateContactData = updateContactData;
	this.dismissWarning = dismissWarning;
	this.init = init;
	init();
};

