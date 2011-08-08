<%@ page contentType="text/html;charset=UTF-8" %>
<div id="tabs" class="vertical-tabs">
		<ol>
			<li><a href="#tabs-1">Enter Question</a></li>
			<li><a href="#tabs-2">Answer list</a></li>
			<li><a href="#tabs-3">Automatic reply</a></li>
			<li><a href="#tabs-4">Select recipients</a></li>
			<li><a href="#tabs-5">Confirm</a></li>
		</ol>

	<g:form action="save" name="poll-details" controller="poll" method="post">
		<g:render template="question"/>
		<g:render template="answers"/>
		<g:render template="replies"/>
		<div id="tabs-4">
			<g:render template="../quickMessage/select_recipients" model= "['contactList' : contactList,
					'groupList': groupList,
					'nonExistingRecipients': [],
					'recipients': []]"></g:render>
		</div>
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
		} else {
			$("#responses").val("")
		}
	}
	
	function skipTabBy(numberOfTabs, condition) {
		if (condition) moveToTabBy(2)
		else moveToTabBy(1)
	}

	function nextTabToMove() {
		var selectedElements = getSelectedGroupElements('poll-type');
		skipTabBy(2, selectedElements.size() > 0 && selectedElements[0].value == 'standard');
		validatePollResponses();
	}

	function validatePollResponses() {
		$(".choices").each(function() {
			$(this).blur(function() {
				var label = $("label[for='" + this.id + "']");
				if (!$.trim(this.value).length) label.removeClass('bold');
				else label.addClass('bold');
			});
		})
	}
</script>





