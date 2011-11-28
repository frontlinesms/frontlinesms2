<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="smart-group-details" url="${[action:'save', controller:'smartGroup']}" method='post'">
		<div class="error-panel hide"><div id="error-icon"></div>Please fill in all the required fields.  You may only specify one rule per field.</div>
		<p class="info">To create a Smart group, select the criteria you need to be matched for contacts for this group</p>
		<div class="smartgroupname">
			<label class="bold inline" for="smartgroupname"><g:message code="smartgroup.name.label" default="Name" />:</label>
			<g:textField id="smartgroupname-field" class="value ${hasErrors(bean: smartGroupInstance, field: 'smartgroupname', 'errors')}" name="smartgroupname" value="${smartGroupInstance?.name}" />
		</div>
		<table id="smartGroup-table">
			<tbody>
				<tr class="prop smart-group-criteria">
					<td>
						<g:select name="rule-field"
								from="${fieldNames}"
								keys="${fieldIds}"
								onchange="smartGroupCriteriaChanged(this)"/>
					</td>
					<td class="rule-match-text">
						<span class="contains hide">contains</span>
						<span class="starts">starts with</span>
					</td>
					<td>
						<g:textField name="rule-text" class="rule-text"/>
					</td>
					<td>
						<a onclick="removeRule(this)" class="button remove-rule hide"><img class='remove' src='${resource(dir:'images/icons',file:'remove.png')}' /></a>
					</td>
				</tr>
			</tbody>
		</table>
		<a class="button" onclick="addNewRule()">Add another rule</a>
	</g:form>
</div>

<script>
	function removeRule(_removeAnchor) {
		var row = $(_removeAnchor).closest('.smart-group-criteria').remove();
		var rows = $('.smart-group-criteria');
		
		if(rows.length == 1) rows.find('.remove-rule').hide();
	}

	function smartGroupCriteriaChanged(_select) {
		var select = $(_select);
		var row = select.closest('.smart-group-criteria');
		var ruleMatchText = row.find('.rule-match-text');
		if(select.val() == 'mobile') {
			ruleMatchText.find('.starts').show();
			ruleMatchText.find('.contains').hide();
		} else {
			ruleMatchText.find('.starts').hide();
			ruleMatchText.find('.contains').show();
		}
	}
		
	function initializePopup() {}

	function createSmartGroup() {
		$("#submit").attr('disabled', 'disabled');
		if(validateSmartGroup()) {
			$(this).find("form").submit();
			$(this).dialog('close');
		} else {
			$("#submit").removeAttr('disabled');
			$('.error-panel').show();
		}
	}
	
	function validateSmartGroup() {
		var valid = true;
		
		// check the name
		var nameField = $("input[name='smartgroupname']");
		if(nameField.val().length == 0) {
			valid = false;
			nameField.addClass('error');
		} else nameField.removeClass('error');
		
		// iterate over each rule to check for multiple definitions
		var usedFields = new Array();
		var reusedFields = new Array();
		$('.smart-group-criteria select').each(function() {
			var field = $(this).val();
			if(!$.inArray(field, usedFields))
				reusedFields.push(field);
			usedFields.push(field);
		});
		
		$('.smart-group-criteria select').each(function() {
			var field = $(this).val();
			if(!$.inArray(field, reusedFields)) {
				valid = false;
				$(this).addClass('error');
			} else {
				$(this).removeClass('error');
			}
		});
		
		// check for blank rules
		$('input.rule-text').each(function() {
			var i = $(this);
			if(i.val().length == 0) {
				valid = false;
				i.addClass('error');
			} else {
				i.removeClass('error');
			}
		});
		return valid;
	}
	
	function addNewRule() {
		var template = $('.smart-group-criteria').first();
		template.find('.button.remove-rule').show();
		var newRow = template.clone();
		newRow.removeAttr("id");
		newRow.find('.button.remove-rule').show();
		$('form[name="smart-group-details"] tbody').append(newRow);
	}
</script>