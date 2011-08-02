<%@ page contentType="text/html;charset=UTF-8" %>
<div id="create-poll">
	<g:render template="menu"/>
	<div id="poll-wizard-content">
		<g:form action="save" name="poll-details" controller="poll" method="post">
			<g:render template="question"/>
			<g:render template="answers"/>
			<g:render template="replies"/>
			<g:render template="confirm"/>
		</g:form>
	</div>
</div>


<script>
	function validate() {
		return isGroupChecked('auto-reply') ? !isElementEmpty('.check-bound-text-area') : true;
	}

	function populateResponses() {
		if (getSelectedGroupElements('poll-type')[0].value == 'standard') {
			$("#responses").val("yes no");
		}
		else {
			$("#responses").val("")
		}
	}

	function moveForward() {
		var selectedElements = getSelectedGroupElements('poll-type');
		if (selectedElements.size() > 0 && selectedElements[0].value == 'standard') {
			moveToTabBy(2)
		}
		else {
			moveToTabBy(1)
		}
	}
</script>





