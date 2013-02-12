<fsms:step type="webconnectionStep" stepId="${stepId}">
	<div>
		<label for="url"><g:message code="webconnection.url.label"/></label>
		<g:textField name="url" />
	</div>
	<div>
		<g:set var="httpMethod" value="${httpMethod}"/>
		
		<label for="httpMethod"><g:message code="webconnection.httpMethod.get"/></label>
		<g:radio name="httpMethod" value="GET" checked="" />

		<label for="httpMethod"><g:message code="webconnection.httpMethod.post"/></label>
		<g:radio name="httpMethod" value="POST" checked="" />
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
</fsms:step>