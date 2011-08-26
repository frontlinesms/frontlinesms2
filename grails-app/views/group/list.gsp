<%@ page contentType="text/html;charset=UTF-8" %>

<div>
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Select Group</a></li>
			<li><a href="#tabs-3">Confirm</a></li>
		</ul>
		<g:form id="manage-subscription" name="manage-subscription" url="${[action:'update']}" method="post">
			<g:render template="select_group"/>
			<g:render template="confirm"/>
		</g:form>
	</div>
</div>

<script>
	function addTabValidations() {
		$("#tabs-1").contentWidget({
			validate: function() {
				var isValid = validate();
				if (!isValid) {
					displayError();
				} else {
					$('.error-panel').hide();
				}
				return isValid;
			}
		});

		$("#tabs-3").contentWidget({
			onDone: function() {
				return validate();
			}
		});
	}


	function validate() {
		var selectedElements = getSelectedGroupElements('keyword');
		for (var i = 0; i < selectedElements.size(); i++) {
			if (isElementEmpty('$input[checkbox_id=' + selectedElements[i].id + ']'))
				return false;
		}
		return isDropDownSelected("id") && isGroupChecked('keyword')
	}

	function displayError() {
		$('.error-panel').html('<p> please enter all the details </p>').show();
	}
</script>
