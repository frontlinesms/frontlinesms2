<%@ page contentType="text/html;charset=UTF-8" %>
<div>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">Select Group</a></li>
		<li><a href="#tabs-2">Specify Keyword</a></li>
	</ul>
	<g:form action="send" controller="message" method="post">
		<div id="tabs-1">
		<label>Select Group</label>
			<g:each in="${groups}" var="group">
				<div>
					<input type="radio" name="groups" value="${group.id}">${group.name}</input>
				</div>
			</g:each>
		<g:link url="#" class="next">Next</g:link>
	</div>
		<div id="tabs-2">
			<label>Select Keyword</label>
		</div>
	</g:form>
</div>
</div>





