<%@ page import="frontlinesms2.Webconnection" %>
<div class="input">
	<label for="url"><g:message code="webconnection.url.label"/></label>
	<g:textField name="url" value="${activityInstanceToEdit?.url}" required="true"/>
</div>
<div class="input">
	<label for="httpMethod"><g:message code="webconnection.httpMethod.label"/></label>
	<ul class="select">
		<g:set var="httpMethod" value="${activityInstanceToEdit?.httpMethod}"/>
		<li>
			<label for="httpMethod"><g:message code="webconnection.httpMethod.get"/></label>
			<g:radio name="httpMethod" value="GET" checked="${!activityInstanceToEdit || httpMethod == Webconnection.HttpMethod.GET}" />
		</li>
		<li>
			<label for="httpMethod"><g:message code="webconnection.httpMethod.post"/></label>
			<g:radio name="httpMethod" value="POST" checked="${activityInstanceToEdit && httpMethod != Webconnection.HttpMethod.GET}" />
		</li>
	</ul>
</div>
<h2><g:message code="webconnection.parameters"/></h2>
<table id="web-connection-param-table">
	<thead>
		<tr class="prop">
			<td>
				<label for="param-name"><g:message code="webconnection.param.name"/></label>
			</td>
			<td>
				<label for="param-value"><g:message code="webconnection.param.value"/></label>
			</td>
		</tr>
	</thead>
	<tbody>
		<g:if test="${activityInstanceToEdit?.id}">
			<g:if test="${activityInstanceToEdit?.requestParameters}">
				<g:each in="${activityInstanceToEdit?.requestParameters}" var="parameter" status="i">
					<fsms:render template="/webconnection/generic/parameter" model="[name:parameter.name, value:parameter.value]" />
				</g:each>
			</g:if>
			<g:else>
				<fsms:render template="/webconnection/generic/parameter" model="[name:'',  value:'']"/>
			</g:else>
		</g:if>
		<g:else>
			<fsms:render template="/webconnection/generic/parameter" model="[name:'message',  value:'${message_body}']"/>
		</g:else>
	</tbody>
</table>
<a class="btn addNew" onclick="webconnectionDialog.handlers.addNewParam()">
	<g:message code="webconnection.add.anotherparam"/>
</a></br>
