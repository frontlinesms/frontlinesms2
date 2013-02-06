<li class='webconnection-action-step step' index='${stepId}'>
	<div><a class='remove-command remove-step'></a></div>
	<span>Upload Message to Url</span>
	<g:hiddenField name='stepId' value="${stepId}"/>
	<g:hiddenField name='stepType' value='webconnectionStep'/>
	<br>
	<g:textField name="url" />

	<ul class="select">
		<g:set var="httpMethod" value="${httpMethod}"/>
		<li>
			<label for="httpMethod"><g:message code="webconnection.httpMethod.get"/></label>
			<g:radio name="httpMethod" value="GET" checked="" />
		</li>
		<li>
			<label for="httpMethod"><g:message code="webconnection.httpMethod.post"/></label>
			<g:radio name="httpMethod" value="POST" checked="" />
		</li>
	</ul>

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
			<g:if test="${stepInstance?.id}">
				<g:if test="${stepInstance.params}">
					<g:each in="${stepInstance?.params}" var="parameter" status="i">
						<fsms:render template="/webconnection/parameter" model="[name:parameter.name, value:parameter.value]" />
				</g:each>
				</g:if>
				<g:else>
					<fsms:render template="/webconnection/parameter" model="[name:'',  value:'']"/>
				</g:else>
			</g:if>
			<g:else>
				<fsms:render template="/webconnection/parameter" model="[name:'message',  value:'${message_body}']"/>
			</g:else>
		</tbody>
	</table>
</li>

