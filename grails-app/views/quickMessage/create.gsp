<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<div id="tabs">
	<ol>
		<g:each in="['tabs-1' : 'Enter Message', 'tabs-2' : 'Select Recipients', 'tabs-3' : 'Confirm']" var='entry' >
			<g:if test="${configureTabs.contains(entry.key)}">
				<li><a href="#${entry.key}" >${entry.value}</a></li>
			</g:if>
		</g:each>
	</ol>

	<g:form action="send" controller="message" method="post">
		<g:render template="message"/>
		<div id="tabs-2" class="${configureTabs.contains("tabs-2") ? "" : "hide"}">
			<label for="address">Add phone number</label>
			<g:textField id="address" name="address"/>
			<g:link url="#" class="add-address">Add</g:link>
			<g:render template="select_recipients"/>
		</div>
		<g:render template="confirm"/>
	</g:form>
</div>


