<%@ page contentType="text/html;charset=UTF-8" %>

<div>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">Select Group</a></li>
		<li><a href="#tabs-2">Specify Keyword</a></li>
	</ul>
	<g:form name="manage-subscription" url="${[action:'update']}" method="post" onsubmit='return validate();'>
		<div id="tabs-1">
		<label>Select Group</label>
				<div>
					<g:radioGroup name="id" labels="${groups.collect{it.name}}" values="${groups.collect{ it.id}}">
					<p>${it.radio} ${it.label}</p>
					</g:radioGroup>	
				</div>
		<g:link url="#" class="next">Next</g:link>
	</div>
		<div id="tabs-2">
			<div class='error'></div>
			<div> <label>Subscribe Keyword</label> <g:textField name="subscriptionKey"/> </div>
			<div> <label>Unsubscribe Keyword</label> <g:textField name="unsubscriptionKey" /> </div>
			<g:submitButton name="submit">Submit</g:submitButton>
		</div>
	</g:form>
</div>
</div>

 <script>
	function validate() {
		var isValid = isRadioGroupChecked("id") && (!isElementEmpty('#subscriptionKey')) && (!isElementEmpty('#unsubscriptionKey'));
		if (!isValid) {
			$('.error').html("please enter all the details");
		}
		return isValid;
	}
</script>
