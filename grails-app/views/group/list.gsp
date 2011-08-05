<%@ page contentType="text/html;charset=UTF-8" %>

<div>
	<div id="tabs">
		<ul>
			<li><a href="#tabs-1">Select Group</a></li>
			<li><a href="#tabs-2">Automatic Reply</a></li>
			<li><a href="#tabs-3">Confirm</a></li>
		</ul>
		<g:form id="manage-subscription" name="manage-subscription" url="${[action:'update']}" method="post" onsubmit='return validate();'>
			<g:render template="select_group"/>
			<g:render template="auto_reply"/>
			<g:render template="confirm"/>                                            
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
