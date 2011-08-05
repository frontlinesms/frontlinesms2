<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
		<ol>
			<li><a href="#tabs-1">Enter Question</a></li>
			<li><a href="#tabs-2">Answer list</a></li>
			<li><a href="#tabs-3">Automatic reply</a></li>
			<li><a href="#tabs-4">Confirm</a></li>
		</ol>

	<g:form action="save" name="poll-details" controller="poll" method="post">
		<g:render template="question"/>
		<g:render template="answers"/>
		<g:render template="replies"/>
		<g:render template="confirm"/>
	</g:form>
</div>

<script>
	function validate() {
		alert('validating');
		return isGroupChecked('auto-reply') ? !isElementEmpty('.check-bound-text-area') : true;
	}

	function populateResponses() {
		if (getSelectedGroupElements('poll-type')[0].value == 'standard') {
			$("#responses").val("yes no");
		}
		else {
			$("#responses").val("");
		}
	}
</script>





