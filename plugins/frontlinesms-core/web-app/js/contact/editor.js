var contactEditor;
$(function() {
	contactEditor = new ContactEditor();
});

var ContactEditor = function() {
	var cachedFormHash,
	fieldsToAdd = [], fieldsToRemove = [],
	contactEditForm = $(".contact-edit-form"),
	updateInProgress, updateRequested,
	updateContactData = function(event) {
		if(!contactEditForm.valid()) {
			return false;
		}
		var formData = contactEditForm.serialize(),
		formHashAtRequestTime = formData.hashCode();
		if(formHashAtRequestTime !== cachedFormHash) {
			$.ajax({
				type:"POST",
				url:url_root + "contact/saveContact",
				data:formData,
				beforeSend:function() {
					if(updateInProgress) {
						updateRequested = true;
						return false;
					}
					setUpdateInProgress(true, event.target);
				},
				complete:function() {
					setUpdateInProgress(false, event.target);
					if(updateRequested) {
						updateRequested = false;
						updateContactData(event);
					}
				},
				success:function(data) {
					var contactName, button, buttonKids;
					cachedFormHash = formHashAtRequestTime;
					button = $('#action-buttons .send-message');
					buttonKids = button.children();
					contactName = $(".contact-edit-form [name='name']").val();
					button.text(" " + i18n("contact.send.message", contactName));
					button.prepend(buttonKids);
				}
			});
		}
	},
	setUpdateInProgress = function(inProgress, targetElement) {
		targetElement = $(targetElement)
		updateInProgress = inProgress;
		if(updateInProgress) {
			targetElement.after("<i class='update-in-progress'/>");
		} else {
			targetElement.parent().find(".update-in-progress").fadeOut(2000);
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

	function validateMobile(field) {
		var internationFormatWarning, val, sendMessageButton;
		field = $(this);
		internationFormatWarning = field.parent().find(".warning"),
		val = field.val();
		if(!val || val.match(/\+\d+/)) {
			field.removeClass("error");
			internationFormatWarning.hide("fast");
		} else {
			field.addClass("error");
			internationFormatWarning.show("fast");
		}

		sendMessageButton = $("#action-buttons a.send-message");
		if(val) {
			sendMessageButton.show("fast");
		} else {
			sendMessageButton.hide("fast");
		}
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
					$("#smallpopup-error-panel").html(i18n("customfield.validation.error"));
					$("#smallpopup-error-panel").show();
					return false;
				}
			}
			addCustomField(name);
			$("#modalBox").remove();
		} else {
			$("#smallpopup-error-panel").html(i18n("customfield.validation.prompt"));
			$("#smallpopup-error-panel").show();
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

//> INITIALISE
	function init() {
		cachedFormHash = contactEditForm.serialize().hashCode();
		$("#notes").autosize();

		$("input[name=mobile]").change(validateMobile);

		// bind form data change listeners
		$(".edit input[type=text], .edit textarea").blur(updateContactData);
		$(".edit input[type=hidden], .edit select").change(updateContactData);

		contactEditForm.bind("addedCustomFieldToContact", function() {
			$(".edit input[type=text]").blur(updateContactData);
			$(".edit .custom-field .remove-command").click(removeCustomFieldClickHandler);
		});
		$(".edit .custom-field .remove-command").click(removeCustomFieldClickHandler);

		$("#new-field-dropdown").change(addFieldClickAction);
	}

	this.updateContactData = updateContactData;
	this.init = init;
	init();
};

