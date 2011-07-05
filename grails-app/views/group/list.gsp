<%@ page contentType="text/html;charset=UTF-8" %>
<div>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">Select Group</a></li>
		<li><a href="#tabs-2">Specify Keyword</a></li>
	</ul>
	<g:formRemote name="manage-subscription" url="${[action:'update']}" method="post">
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
			<div> <label>Subscribe Keyword</label> <g:textField name="subscribeKeyword"/> </div>
			<div> <label>Unsubscribe Keyword</label> <g:textField name="unsubscribeKeyword" /> </div>
			<g:submitButton name="submit">Submit</g:submitButton>
		</div>
	</g:formRemote>
</div>
</div>





