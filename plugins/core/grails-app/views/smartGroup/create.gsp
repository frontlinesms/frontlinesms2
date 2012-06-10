<%@ page contentType="text/html;charset=UTF-8" %>
<meta name="layout" content="popup"/>
<div>
	<div class="error-panel hide">
	<div id="error-icon"></div>
		<g:message code="smartgroup.validation.prompt"/>
	</div>
	<g:form name="smart-group-details" url="${[action:'save', controller:'smartGroup', id:smartGroupInstance?.id]}" method="post">
		<p class="info">
			<g:message code="smartgroup.info"/>
		</p>
		<table id="smartGroup-table">
			<tbody>
				<tr class="name">
					<td class="label">
						<label class="bold inline" for="smartgroupname"><g:message code="smartgroup.name.label" default="Name"/></label>
					</td>
					<td></td>
					<td>
						<g:textField id="smartgroupname-field" class="value ${hasErrors(bean: smartGroupInstance, field: 'smartgroupname', 'errors')}" name="smartgroupname" value="${smartGroupInstance?.name}"/>
					</td>
				</tr>
				<g:if test="${smartGroupInstance.id}">
					<g:each in="${currentRules.keySet()}" var="field" status="i">
						<g:set var="isFirst" value="i==0"/>
						<g:if test="${field == 'customFields'}">
							<g:each in="${currentRules.customFields}" var="customField">
								<g:render template="rule" model="[key:customField.name, value:customField.value, isFirst:isFirst]"/>
							</g:each>
						</g:if>
						<g:else>
							<g:render template="rule" model="[key:field, value:currentRules[field], isFirst:isFirst]"/>
						</g:else>
					</g:each>
				</g:if>
				<g:else>
					<g:render template="rule" model="[isFirst:true, key:'mobile']"/>
				</g:else>
			</tbody>
		</table>
		<a class="btn" onclick="addNewRule()">
			<g:message code="smartgroup.add.anotherrule"/>
		</a></br>
	</g:form>
</div>

<r:script>
	function removeRule(_removeAnchor) {
		var row = $(_removeAnchor).closest('.smart-group-criteria').remove();
		var rows = $('.smart-group-criteria');
		
		if(rows.length == 1) rows.find('.remove-command').hide();
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
		
	function initializePopup(dialog) {
		dialog.find("select").selectmenu();
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
		var templateSelect = template.find("select");
		templateSelect.selectmenu("destroy");
		template.find('.remove-command').show();
		var newRow = template.clone();
		templateSelect.selectmenu();
		newRow.removeAttr("id");
		newRow.find('input.rule-text').val("");
		newRow.find('.remove-command').show();
		$('form[name="smart-group-details"] tbody').append(newRow);
		newRow.find('select').selectmenu();
	}
</r:script>

