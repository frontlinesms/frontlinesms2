<%@ page contentType="text/html;charset=UTF-8" %>

<div>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">Select Group</a></li>
		<li><a href="#tabs-2">Automatic Reply</a></li>
		<li><a href="#tabs-2">Confirm</a></li>
	</ul>
	<g:form name="manage-subscription" url="${[action:'update']}" method="post" onsubmit='return validate();'>
		<div id="tabs-1">
			<label>Select Group</label>
			<div class='error'></div>
			Contacts can be added and removed from groups automatically when FrontlineSMS receives a message that includes a special keyword
			<g:select name="id" from="${groups.collect{it.name}}" keys="${groups.collect{it.id}}" noSelection="['':'Select group...']"/>

			<div>
				<div>Join Keyword</div>
				<g:checkBox name="keyword" value="subscriptionKey" checked='false'/> Join the group using a keyword
				<g:textField name="subscriptionKey"/>
			</div>

			<div>
				<div>Leave Keyword</div>
				<g:checkBox name="keyword" value="unsubscriptionKey" checked='false'/> Leave the group using a keyword
				<g:textField name="unsubscriptionKey"/>
			</div>
			<g:link url="#" class="next">Next</g:link>
		</div>
		<div id="tabs-2"></div>
		<div id="tabs-3">
			<g:submitButton name="submit">Submit</g:submitButton>
		</div>
	</g:form>
</div>
</div>

 <script>
	function validate() {
		var selectedElements = getSelectedGroupElements('keyword');
		for(i = 0; i < selectedElements.size(); i++){
			if(isElementEmpty('#' + selectedElements[i].value))
				return false;
		}
		return isDropDownSelected("id") && isGroupChecked('keyword')
	}
</script>
