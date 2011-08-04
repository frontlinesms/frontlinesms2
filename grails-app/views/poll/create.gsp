<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
		<ol>
			<li><a href="#tabs-1" onclick="popupButtons()">Enter Question</a></li>
			<li><a href="#tabs-2" onclick="popupButtons()">Answer list</a></li>
			<li><a href="#tabs-3" onclick="popupButtons()">Automatic reply</a></li>
			<li><a href="#tabs-4" onclick="popupButtons()">Confirm</a></li>
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
</script>





