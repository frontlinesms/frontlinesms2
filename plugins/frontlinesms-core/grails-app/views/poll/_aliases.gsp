<div id="sorting">
	<h2><g:message code="poll.sort.header"/></h2>
	<div class="info">
		<p><g:message code="poll.sort.description"/></p>
	</div>
	<div class="input">
		<ul class="select">
			<li>
				<label for="enableKeyword"><g:message code="poll.autosort.description"/></label>
				<g:radio name="enableKeyword" id="yesAutosort" value="true" checked="${activityInstanceToEdit?.keywords? activityInstanceToEdit.keywords as boolean: ''}"/>
			</li>
			<li>
				<label for="enableKeyword"><g:message code="poll.autosort.no.description"/></label>
				<g:radio name="enableKeyword" id="noAutosort" value="false" checked="${activityInstanceToEdit?.keywords? '': true}"/>
			</li>
		</ul>
	</div>

	<r:script>
		var enableKeyword = function() {
				var enabled = $(this).val();
				if(enabled == "true") {
					$('#poll-keyword').removeAttr("disabled");
					$('#poll-keyword').show();
					$('#poll-keywords').show();
					$('input:not(:disabled).keywords').addClass('required');
				}
				else {
					$('#poll-keyword').attr("disabled", "disabled");
					$('#poll-keyword').hide();
					$('#poll-keywords').hide();
					$('input:not(:disabled).keywords').removeClass('required');
				}
			};
		$("input[name='enableKeyword']").live("change", enableKeyword);
	</r:script>

</div>

<div id="poll-keywords">
	<h2><g:message code="poll.sort.enter.keywords"/></h2>
	<div class="info">
		<p><g:message code="poll.keywords.prompt.details"/></p>
		<br>
		<p><g:message code="poll.keywords.prompt.more.details"/></p>
	</div>

	<div style="padding-bottom:5px;">
		<label for="topLevelKeyword"><g:message code="poll.sort.toplevel.keyword.label"/></label>
		<g:textField placeholder="${g.message(code:'poll.sort.example.toplevel')}" name="topLevelKeyword" id="poll-keyword" class="sorting-generic-no-spaces sorting-generic-unique validcommas" disabled="${activityInstanceToEdit?.keywords?false:true}" value="${activityInstanceToEdit?.keywords.findAll{it.isTopLevel && it.ownerDetail == null}?.value?.join(',')}"/>
	</div>
	<p<g:message code="poll.keywords.response.label"/></p>
	<div class="input">
		<ul id='poll-aliases'>
			<g:each in="${['A','B','C','D','E']}" var="key" status="i">
				<li>
					<g:if test="${activityInstanceToEdit?.id}">
						<label for='keywords${key}' class="${key == 'A' || key == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 1)) ? 'field-enabled': ''}">keywords${key}</label>
						<% def pollResponse = activityInstanceToEdit?.responses.find {it.key == key} %>
						<g:if test="${(key == 'A' || key == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 2)))}">
							<g:textField placeholder="${g.message(code:'poll.sort.example.keywords.'+key)}" class='keywords required validcommas sorting-generic-no-spaces' name='keywords${key}' value="${activityInstanceToEdit.keywords.findAll{ it.ownerDetail == pollResponse?.key }.value.join(',')}"/>
						</g:if>
						<g:else>
							<g:textField placeholder="${g.message(code:'poll.sort.example.keywords.'+key)}" class='keywords required validcommas sorting-generic-no-spaces' name="keywords${key}" value="" disabled="true"/>
						</g:else>
					</g:if>
					<g:else>
						<label for='keywords${key}' class="${key == 'A' || key == 'B' ? 'field-enabled': ''}">${option}</label>
						<g:if test="${key == 'A' || key == 'B'}">
							<g:textField placeholder="${g.message(code:'poll.sort.example.keywords.yn.'+key)}" class='keywords validcommas sorting-generic-no-spaces' name='keywords${key}'/>
						</g:if>
						<g:else>
							<g:textField class='keywords validcommas sorting-generic-no-spaces' name='keywords${key}' disabled="true"/>
						</g:else>
					</g:else>
				</li>
			</g:each>
		</ul>
	</div>
</div>
