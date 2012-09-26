<%@ page import="frontlinesms2.WebConnection" %>
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
			<g:radio name="httpMethod" value="GET" checked="${!activityInstanceToEdit || httpMethod == WebConnection.HttpMethod.GET}" />
		</li>
		<li>
			<label for="httpMethod"><g:message code="webConnection.httpMethod.post"/></label>
			<g:radio name="httpMethod" value="POST" checked="${activityInstanceToEdit && httpMethod != WebConnection.HttpMethod.GET}" />
		</li>
	</ul>
</div>
<h2><g:message code="webConnection.parameters"/></h2>
<table id="web-connection-param-table">
	<thead>
		<tr class="prop">
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
			<g:if test="${activityInstanceToEdit?.requestParameters}">
				<g:each in="${activityInstanceToEdit?.requestParameters}" var="parameter" status="i">
					<fsms:render template="/webConnection/parameter" model="[name:parameter.name, value:parameter.value]" />
				</g:each>
			</g:if>
			<g:else>
				<fsms:render template="/webConnection/parameter" model="[name:'',  value:'']"/>
			</g:else>
		</g:if>
		<g:else>
			<fsms:render template="/webConnection/parameter" model="[name:'message',  value:'${messageText}']"/>
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
			if($('.web-connection-parameter').length === 1) {
				row.addClass("disabled");
				row.find("input").removeClass("error");
				row.find("input").attr("disabled", "disabled");
				row.hide();
			} else { row.remove();}
		}

	function autofillValue(list) {
		var varName = $(list).val();
		if(varName !== "na") {
			$(list).parents(".web-connection-parameter").find("input[name=param-value]").val("\$\{" + varName + "\}");
		}
		$(list).trigger("keyup");
	}

	function addNewParam() {
		if($('.web-connection-parameter:hidden').length === 1) {
				$('.web-connection-parameter').show();
				$('.web-connection-parameter').removeClass("disabled");
				$('.web-connection-parameter').find("input").attr("disabled", false);
				return;
		}
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
		var httpMethod = $("input[name=httpMethod]:checked").val().toUpperCase();
		var requestParameters = "";
		if($(".web-connection-parameter.disabled").is(":hidden")) { 
			requestParameters = i18n("webConnection.none.label")
		} else {
			$('input[name=param-name]').each(function(index) {
				var values = $('input[name=param-value]').get();
				if($(this).val().length > 0) {
					requestParameters += '<p>' + $(this).val() + ':' + $(values[index]).val() + '</p>';
				}
			});
		}
		$("#url-confirm").html('<p>' + url  + '</p>');
		$("#httpMethod-confirm").html('<p>' + httpMethod  + '</p>');
		$("#requestParameters-confirm").html('<p>' + requestParameters  + '</p>');

	}
</r:script>