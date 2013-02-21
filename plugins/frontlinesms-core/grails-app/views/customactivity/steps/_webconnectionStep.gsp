<%@ page import="frontlinesms2.WebconnectionActionStep" %>
<fsms:step type="webconnectionStep" stepId="${stepId}">
	<div class='webconnection-server-control'>
		<label for="url"><g:message code="webconnection.url.label"/>
			<g:textField name="url" id="" class="customactivity-field" value="${urlEncode}"/>
		</label>

		
		<label>
			<g:radio name="httpMethod" id="" class="customactivity-field" value="GET" checked="${(httpMethod == 'GET')?'checked':''}" />
			<g:message code="webconnection.httpMethod.get"/>
		</label>

		<label>
			<g:radio name="httpMethod" id="" class="customactivity-field" value="POST" checked="${(httpMethod == 'POST')?'checked':''}" />
			<g:message code="webconnection.httpMethod.post"/>
		</label>
	</div>

	<table class="web-connection-param-table">
		<thead>
			<tr class="prop">
				<td>
					<g:message code="webconnection.param.name"/>
				</td>
				<td>
					<g:message code="webconnection.param.value"/>
				</td>
			</tr>
		</thead>
		<tbody>
			<g:if test="${stepId}">
				<g:if test="${requestParameters}">
					<g:each in="${requestParameters}" var="parameter" status="i">
						<fsms:render template="/customactivity/steps/webconnectionStepParameter" model="[name:parameter.name, value:parameter.value]" />
					</g:each>
				</g:if>
				<g:else>
					<fsms:render template="/customactivity/steps/webconnectionStepParameter" model="[name:'',  value:'']"/>
				</g:else>
			</g:if>
			<g:else>
				<fsms:render template="/customactivity/steps/webconnectionStepParameter" model="[name:'message',  value:'${message_body}']"/>
			</g:else>
		</tbody>
	</table>
</fsms:step>
