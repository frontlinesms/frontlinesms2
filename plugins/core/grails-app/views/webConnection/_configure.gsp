<div class="input">
	<label for="url"><g:message code="webConnection.url.label"/></label>
	<g:textField name="url" value="${activityInstanceToEdit?.url}" required="true"/>
</div>
<div class="input">
	<label for="httpMethod"><g:message code="webConnection.httpMethod.label"/></label>
	<ul class="select">
		<g:set var="httpMethod" value="${activityInstanceToEdit?.httpMethod}"/>
		<li>
			<label for="httpMethod"><g:message code="webConnection.httpMethod.get"/></label>
			<g:radio name="httpMethod" value="GET" checked="${!activityInstanceToEdit || httpMethod}" disabled="${activityInstanceToEdit && !httpMethod}"/>
		</li>
		<li>
			<label for="httpMethod"><g:message code="webConnection.httpMethod.post"/></label>
			<g:radio name="httpMethod" value="POST" checked="${activityInstanceToEdit && !httpMethod}" disabled="${activityInstanceToEdit && httpMethod}"/>
		</li>
	</ul>
</div>
<h2><g:message code="webConnection.parameters"/></h2>
<g:set var="isFirst" value="i==0"/>
<table id="web-connection-param-table">
	<thead>
		<tr class="prop web-connection-parameter">
			<td>
				<label for="param-name"><g:message code="webConnection.param.name"/></label>
			</td>
			<td>
				<label for="param-value"><g:message code="webConnection.param.value"/></label>
			</td>
		</tr>
	</thead>
	<tbody>
		<g:if test="${activityInstanceToEdit?.id}">
			<g:each in="${activityInstanceToEdit?.requestParameters}" var="parameter" status="i">
				<g:set var="isFirst" value="i==0"/>
				<fsms:render template="/webConnection/parameter" model="[name:parameter.name, value:parameter.value, isFirst:isFirst]" />
			</g:each>
		</g:if>
		<g:else>
			<fsms:render template="/webConnection/parameter" model="[name:'message',  value:'${messageText}', isFirst:true]"/>
		</g:else>
	</tbody>
</table>
<a class="btn addNew" onclick="addNewParam()">
	<g:message code="webConnection.add.anotherparam"/>
</a></br>


<r:script>

	function removeRule(_removeAnchor) {
			var row = $(_removeAnchor).closest('.web-connection-parameter');
			if(row.find("#param-name.error").is(":visible") && $(".error").size() < 4) { $(".error-panel").hide(); }
			row.remove();
			var rows = $('.web-connection-parameter');
			if(rows.length == 2) rows.find('.remove-command').hide();
		}

	function autofillValue(list) {
		var varName = $(list).val();
		if(varName !== "na") {
			$(list).parents(".web-connection-parameter").find("input[name=param-value]").val("\$\{" + varName + "\}");
		}
		$(list).trigger("keyup");
	}

	function addNewParam() {
		var template = $('.web-connection-parameter').last();
		var target = "param.value";
		// Selectmenu is destroyed here to allow cloning. Rebuilt after clone
		if($(".error").size() > 1) { $(".error-panel").show(); }
		template.find("select").selectmenu("destroy");
		template.find('.remove-command').show();
		var newRow = template.clone();
		newRow.removeAttr("id");
		newRow.find('input.param-name').val("");
		newRow.find('input.param-value').val("");
		newRow.find('.remove-command').show();
		$('#web-connection-param-table tbody').append(newRow);
		magicwand.init(newRow.find('select[id^="magicwand-select"]'));
		magicwand.reset(template.find("select"));
	}

	function updateServerConfiguration() {
		var url = $("input[name=url]").val();
		var httpMethod = $("input[name=httpMethod]").val().toUpperCase();
		var requestParameters = "";

		if(requestParameters.length === 0) { requestParameters = i18n("webConnection.none.label")}
		$("#url-confirm").html('<p>' + url  + '</p>');
		$("#httpMethod-confirm").html('<p>' + httpMethod  + '</p>');

		$('input[name=param-name]').each(function(index) {
			var values = $('input[name=param-value]').get();
			if($(this).val().length > 0) {
				requestParameters += '<p>' + $(this).val() + ':' + $(values[index]).val() + '</p>';
			}
		});
		$("#requestParameters-confirm").html('<p>' + requestParameters  + '</p>');

	}
</r:script>