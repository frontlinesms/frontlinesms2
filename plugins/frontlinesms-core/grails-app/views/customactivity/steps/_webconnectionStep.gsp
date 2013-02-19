<%@ page import="frontlinesms2.WebconnectionActionStep" %>
<fsms:step type="webconnectionStep" stepId="${stepId}">
	<div class='webconnection-server-control'>
		<label for="url"><g:message code="webconnection.url.label"/></label>
		<g:textField name="url" id="" class="customactivity-field" value="${urlEncode}" size="30"/>


		<g:set var="httpMethod" value="${httpMethod}"/>
		
		<label for="httpMethod">
			<g:radio name="httpMethod" id="" class="customactivity-field" value="GET" checked="${(httpMethod == 'GET')?'checked':''}" />
			<g:message code="webconnection.httpMethod.get"/>
		</label>

		<label for="httpMethod">
			<g:radio name="httpMethod" id="" class="customactivity-field" value="POST" checked="${(httpMethod == 'POST')?'checked':''}" />
			<g:message code="webconnection.httpMethod.post"/>
		</label>
	</div>

	<table class="web-connection-param-table">
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
			<g:if test="${stepId}">
				<g:if test="${requestParameters}">
					<g:each in="${requestParameters}" var="parameter" status="i">
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
	<a class="btn addNew" onclick="webconnectionDialog.handlers.addNewParam($(this).parent().find('.web-connection-param-table'))">
		<g:message code="webconnection.add.anotherparam"/>
	</a>
</fsms:step>
