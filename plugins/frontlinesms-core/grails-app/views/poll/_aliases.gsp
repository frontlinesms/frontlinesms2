<div id="sorting">
	<h2><g:message code="poll.sort.header"/></h2>
	<div class="info">
		<p><g:message code="poll.sort.description"/></p>
	</div>
	<div class="input">
		<ul class="select">
			<li>
				<label for="enableKeyword"><g:message code="poll.autosort.no.description"/></label>
				<g:radio name="enableKeyword" value="false" checked="${activityInstanceToEdit?.keywords? activityInstanceToEdit.keywords as boolean: true}"/>
			</li>
			<li>
				<label for="enableKeyword"><g:message code="poll.autosort.description"/></label>
				<g:radio name="enableKeyword" value="true" checked="${activityInstanceToEdit?.keywords? activityInstanceToEdit.keywords as boolean: false}"/>
				<g:textField name="topLevelKeyword" id="poll-keyword" class="no-space" disabled="${activityInstanceToEdit?.keywords?false:true}" value="${activityInstanceToEdit?.keywords.find{it.isTopLevel && it.ownerDetail == null}?.value}"/>
			</li>
		</ul>
	</div>

	<r:script>
		var enableKeyword = function() {
				var enabled = $(this).val();
				if(enabled == "true") {
					mediumPopup.enableTab("poll-alias");
					$('#poll-keyword').removeAttr("disabled");
				}
				else {
					$('#poll-keyword').attr("disabled", "disabled");
				}
			};
		$("input[name='enableKeyword']").live("change", enableKeyword);
		$('#poll-keyword').addClass("required");
	</r:script>



</div>
<div class="input">
	<label for='poll-aliases'><g:message code="poll.aliases.prompt"/></label>
	<div class="info">
		<p><g:message code="poll.aliases.prompt.details"/></p>
	</div>
	<ul id='poll-aliases'>
		<g:each in="${['A','B','C','D','E']}" var="key" status="i">
			<li>
				<g:if test="${activityInstanceToEdit?.id}">
					<label for='keywords${key}' class="${key == 'A' || key == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 1)) ? 'field-enabled': ''}">keywords${key}</label>
					<% def pollResponse = activityInstanceToEdit?.responses.find {it.key == key} %>
					<g:if test="${(key == 'A' || key == 'B' || pollResponse?.value || (i == (activityInstanceToEdit?.responses.size() - 2)))}">
						<g:textField class='keywords validcommas' name='keywords${key}' value="${activityInstanceToEdit.keywords.findAll{ it.ownerDetail == pollResponse?.id.toString() }.value.join(',')}"/>
					</g:if>
					<g:else>
						<g:textField class='keywords validcommas' name="keywords${key}" value="${activityInstanceToEdit.keywords.findAll{ it.ownerDetail == pollResponse?.id.toString() }.value.join(',')}" disabled="true"/>
					</g:else>
				</g:if>
				<g:else>
					<label for='keywords${key}' class="${key == 'A' || key == 'B' ? 'field-enabled': ''}">${option}</label>
					<g:if test="${key == 'A' || key == 'B'}">
						<g:textField class='keywords validcommas' name='keywords${key}'/>
					</g:if>
					<g:else>
						<g:textField class='keywords validcommas' name='keywords${key}' disabled="true"/>
					</g:else>
				</g:else>
			</li>
		</g:each>
	</ul>
</div>
