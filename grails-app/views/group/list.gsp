<%@ page contentType="text/html;charset=UTF-8" %>

<div>
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Select Group</a></li>
			<li><a href="#tabs-3">Confirm</a></li>
		</ul>
		<g:form id="manage-subscription" name="manage-subscription" url="${[action:'update']}" method="post">
			<div class="error-panel hide subscription">please enter all the details</div>
			<g:render template="select_group"/>
			<g:render template="confirm"/>
		</g:form>
	</div>
</div>

<g:javascript>
	function addTabValidations() {
		$("#tabs-1").contentWidget({
			validate: function() {
				$("#subscriptionKey").removeClass('error');
				$("#unsubscriptionKey").removeClass('error');
				return validate();
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
		var isValid = true;
		for (var i = 0; i < selectedElements.size(); i++) {
			var inputKeyword =  $("#" + selectedElements[i].value);
			if (isEmpty(inputKeyword.val())) {
				inputKeyword.addClass('error');
				isValid = false;
			}
		}
		return isDropDownSelected("id") && isValid
	}

	function displayError() {
		$('.error-panel').html('<p> please enter all the details </p>').show();
	}
</g:javascript>
