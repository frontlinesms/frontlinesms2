var subscription = (function() {
	var 
		initializeTabValidation = function(validator) {
			var 
				groupTabValidation = function() {
					return validator.element($('#subscriptionGroup'));
				},
				sortingTabValidation = function() {
					var valid = true;
					$('input:not(:disabled).keywords').each(function() {
						if (!validator.element(this) && valid) {
						    valid = false;
						}
					});
					return validator.element('#topLevelKeywords') && valid;
				},
				autoreplyTabValidation = function() {
					var valid = true;
					if($('#joinAutoreplyText').attr('disabled') !== 'disabled'){
						valid = valid && validator.element($('#joinAutoreplyText'));
					}
					if($('#leaveAutoreplyText').attr('disabled') !== 'disabled'){
						valid = valid && validator.element($('#leaveAutoreplyText'));
					}
					return valid;
				},
				confirmTabValidation = function() {
					return validator.element('#name');
				};

			mediumPopup.addValidation('subscription-group-header', groupTabValidation);
			mediumPopup.addValidation('subscription-sorting', sortingTabValidation);
			mediumPopup.addValidation('subscription-autoreplies', autoreplyTabValidation);
			mediumPopup.addValidation('subscription-confirm', confirmTabValidation);
			
			$("#tabs").bind("tabsshow", function(event, ui) {
				updateConfirmTab();
			});
		},
		createFormValidator = function() {
			var validator = $("#create_subscription").validate({
				errorContainer: ".error-panel",
				rules: {
					addresses: {
						required: true,
						minlength: 1
					},
					messageText: {
						required:true
					}
				},
				messages: {
					addresses: {
						required: i18n("poll.recipients.validation.error")
					},
					"keyword": {
						required: i18n("subscription.keyword.required")
					},
					"joinAutoreplyText": {
						required: i18n("subscription.jointext.required")
					},
					"leaveAutoreplyText": {
						required: i18n("subscription.leavetext.required")
					}
				},
				errorPlacement: function(error, element) {
					if (element.attr("name") === "addresses") {
						error.insertAfter("#recipients-list");
						$("#recipients-list").addClass("error");
					} else {
						error.insertAfter(element);
					}
				}
			});
			return validator;
		},
		addCustomValidationClasses = function() {
			jQuery.validator.addMethod("not-empty", function(value, element) {
				return ($('#subscriptionGroup').val() !== '' && $('#subscriptionGroup').val() != 'create_group');
			}, i18n("subscription.group.required.error"));
			
			aliasCustomValidation();
			genericSortingValidation();
		},
		updateConfirmTab = function() {
			$("#confirm-group-text").html($("#subscriptionGroup option:selected").text()|| i18n("announcement.message.none"));
			$("#confirm-keyword-text").html($("#topLevelKeywords").val() || i18n("announcement.message.none"));
			$("#confirm-join-alias-text").html($("#joinKeywords").val() || i18n("announcement.message.none"));
			$("#confirm-leave-alias-text").html($("#leaveKeywords").val() || i18n("announcement.message.none"));
			$("#confirm-default-action-text").html($("#defaultAction").val() || i18n("announcement.message.none"));
			$("#confirm-join-autoreply-text").html($("#joinAutoreplyText").val().htmlEncode() || i18n("announcement.message.none"));
			$("#confirm-leave-autoreply-text").html($("#leaveAutoreplyText").val().htmlEncode() || i18n("announcement.message.none"));
		},
		selectGroupChangeListener = function() {
			$('#subscriptionGroup').change(function(){
				var addGroup = $('.add-group'), groupNameInput = $(addGroup).find('input[name=groupName]');
				groupNameInput.val('').removeClass('error');
				groupNameInput.parent().find('label.error').remove();
				if($(this).val() === 'create_group' && addGroup.hasClass('hide')) {
					addGroup.removeClass('hide');
				} else if($(this).val() !== 'create_group') {
					addGroup.addClass('hide');
				}
			});
		},
		setAddGroupOnClickHandler = function() {
			$('a.btn.create-group').click(function() {
				var groupName = $('#groupName').val(), 
					subscriptionGroupSelect = $('#subscriptionGroup'),
					createGroupOption,
					optionToAdd;
				if(groupName == '') {
					return;
				}
				$.ajax({
					type:'POST',
					data:{'name': groupName},
					url: url_root + 'group/save',
					success: function(data){
						if(!data.ok) {
							$('#groupName').addClass('error').parent().append('<label for="groupName" class="error">' + i18n('group.name.validator.error') + '</label');
							return;
						}
						optionToAdd = $("<option/>",{
							value: data.id, 
							text:data.name,
							selected:'selected'
						});
						createGroupOption = subscriptionGroupSelect.find('option[value=create_group]').remove();
						subscriptionGroupSelect.append(optionToAdd);
						subscriptionGroupSelect.append(createGroupOption);
						$('.add-group').addClass('hide');
						subscriptionGroupSelect.val(data.id);
						subscriptionGroupSelect.selectmenu();
					}
				});
			});
			$('#groupName').change(function() {
				$(this).removeClass('error');
				$(this).parent().find('label.error').remove();
			});
		},
		init = function() {
			initializeTabValidation(createFormValidator());
			selectmenuTools.initAll("select");
			selectGroupChangeListener();
			setAddGroupOnClickHandler();
			addCustomValidationClasses();
		};
	return {
		init:init
	};
}());
