<%@ page contentType="text/html;charset=UTF-8" %>
<div>
	<g:form name="smart-group-details" controller="smartGroup" action="save" >
		<div class="error-panel hide">Please fill in all the required fields.  You may only specify one rule per field.</div>
		<table>
			<tbody>
				<tr class="prop">
					<td valign="top" class="smartgroupname">
						<label for="smartgroupname"><g:message code="smartgroup.name.label" default="Name" /></label>
					</td>
					<td valign="top" class="value ${hasErrors(bean: smartGroupInstance, field: 'smartgroupname', 'errors')}">
						<g:textField name="smartgroupname" value="${smartGroupInstance?.name}" />
					</td>
					<td></td>
					<td></td>
				</tr>
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
						<a onclick="removeRule(this)" class="button remove-rule hide">remove</a>
					</td>
				</tr>
			</tbody>
		</table>
		<a class="button" onclick="addNewRule()">Add another rule</a>
	</g:form>
</div>

<g:javascript>
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
	
	function initializePopup() {
		$('#submit').bind('click', validates);
	}

	function validates() {
		$("#submit").attr('disabled', 'disabled');
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
			if(!$.inArray(field, usedFields)) {
				reusedFields.push(field);
			}
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

		if(valid) {
			$("#smart-group-details").submit();
			$(this).dialog('close');
			window.location = window.location;
		} else {
			$("#submit").removeAttr('disabled');
			$('.error-panel').show();
		}
	}

	function addNewRule() {
		var template = $('.smart-group-criteria').first();
		template.find('.button.remove-rule').show();
		var newRow = template.clone();
		newRow.removeAttr("id");
		newRow.find('.button.remove-rule').show();
		$('form[name="smart-group-details"] tbody').append(newRow);
	}
</g:javascript>