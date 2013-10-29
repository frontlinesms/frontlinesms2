var contactEditor;
$(function() {
	contactEditor = new ContactEditor();
});

var ContactEditor = function() {
	var cachedFormHash,
	contactEditForm = $(".contact-edit-form"),
	updateInProgress, updateRequested,
	updateContactData = function() {
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
					updateInProgress = true;
				},
				complete:function() {
					updateInProgress = false;
					if(updateRequested) {
						updateRequested = false;
						updateContactData();
					}
				},
				success:function(data) {
					cachedFormHash = formHashAtRequestTime;
					var contactName = $(".contact-edit-form [name='name']").val(),
					buttonLabel = " " + i18n("contact.send.message", contactName);
					$('#action-buttons .send-message').html(buttonLabel);
				}
			});
		}
	},
	removeCustomFieldClickHandler = function() {
		var fieldId, fieldElement, fieldName;
		fieldId = $(this).attr('id').substring('remove-field-'.length);
		fieldElement = $(this).parent().parent();
		fieldName = fieldElement.find('label').text();
		fieldElement.remove();
		$("#new-field-dropdown option[value='na']").after('<option value="'+fieldName+'">'+fieldName+'</option>');
		selectmenuTools.refresh($('#new-field-dropdown'));
		removeFieldFromList(fieldName, 'fieldsToAdd');
		addFieldToList(fieldName, 'fieldsToRemove');
		contactEditor.updateContactData();
		updateContactData();
	};

	function validateMobile(field) {
		var internationFormatWarning, val, sendMessageButton;
		field = $(this);
		internationFormatWarning = field.parent().find(".warning"),
		val = field.val();
		if(!val || val.match(/\+\d+/)) {
			internationFormatWarning.hide("fast");
		} else {
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
		var name, fieldsToAdd, x, y;
		if ($("#custom-field-name").val() !== "") {
			name = $("#custom-field-name").val();
			fieldsToAdd = getFieldList('fieldsToAdd').val().split(",");
			for (y in fieldsToAdd) {
				if(fieldsToAdd[y] !== "") { json.uniqueCustomFields.push(fieldsToAdd[y]); }
			}
			for (x in json.uniqueCustomFields) {
				if (json.uniqueCustomFields[x].toLowerCase() === name.toLowerCase()) {
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
		removeFieldFromList(name, 'fieldsToRemove');
		addFieldToList(name, 'fieldsToAdd');
		$("#info-add").parent().before(sanchez.template("custom-field-input", {name:name, fieldName:"newCustomField-"+name, removerName:""}));
		$(".contact-edit-form").trigger("addedCustomFieldToContact");
	}

	function removeFieldFromList(id, fieldName) {
		var f, oldList, newList;
		f = $('input:hidden[name=' + fieldName + ']');
		oldList = f.val();
		newList = oldList.replace(','+ id +',', ',');
		f.val(newList);
	}
	function addFieldToList(id, fieldName) {
		var f, oldList, newList;
		f = getFieldList(fieldName);
		oldList = f.val();
		newList = oldList + id + ',';
		f.val(newList);
	}
	function getFieldList(fieldName) {
		return $('input:hidden[name=' + fieldName + ']');
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

