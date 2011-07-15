<%@ page contentType="text/html;charset=UTF-8" %>

<div>
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Select Group</a></li>
			<li><a href="#tabs-2">Automatic Reply</a></li>
			<li><a href="#tabs-3">Confirm</a></li>
		</ul>
		<g:form id="manage-subscription" name="manage-subscription" url="${[action:'update']}" method="post" onsubmit='return validate();'>
			<div id="tabs-1">
				<label>Select Group</label>
				<div class='error-panel'></div>
				Contacts can be added and removed from groups automatically when FrontlineSMS receives a message that includes a special keyword
				<div>
					<g:select name="id" from="${groups.collect{it.name}}" keys="${groups.collect{it.id}}" noSelection="['':'Select group...']"/>

					<div>
						<div>Join Keyword</div>
						<g:checkBox name="keyword" value="subscriptionKey" id="join_group_checkbox" checked='false'/> Join the group using a keyword
						<g:textField name="subscriptionKey" class="check-bound-text-area" checkbox_id="join_group_checkbox"/>
					</div>                                               

					<div>
						<div>Leave Keyword</div>
						<g:checkBox name="keyword" value="unsubscriptionKey" id="leave_group_checkbox" checked='false'/> Leave the group using a keyword
						<g:textField name="unsubscriptionKey" class="check-bound-text-area" checkbox_id="leave_group_checkbox"/>
					</div>
					<g:link url="#" class="next-validate" onClick="moveToNextTab(validate(), function(){\$('.error-panel').html('please enter all the details'); })">Next</g:link>
				</div>
			</div>                                                  
			<div id="tabs-2">
				<g:link url="#" class="next">Next</g:link>
			</div>
			<div id="tabs-3">
				<g:submitButton name="submit">Submit</g:submitButton>
			</div>
		</g:form>
	</div>
</div>

<script>
	function validate() {
		var selectedElements = getSelectedGroupElements('keyword');
		for (var i = 0; i < selectedElements.size(); i++) {
			if (isElementEmpty('$input[checkbox_id=' + selectedElements[i].id + ']'))
				return false;
		}
		return isDropDownSelected("id") && isGroupChecked('keyword')
	}
</script>
