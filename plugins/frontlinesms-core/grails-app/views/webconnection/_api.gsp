<%@ page import="frontlinesms2.Webconnection" %>
<div id="webconnection-api">
	<h2><g:message code="webconnection.api"/></h2>
	<div class="info">
		<g:message code="webconnection.api.info"/>
	</div>
	<div class="input">
		<ul class="select">
			<li>
				<g:checkBox name='enableApi' checked="${activityInstanceToEdit?.apiEnabled}"/>
				<label for="enableApi"><g:message code="webconnection.api.enable.label"/></label>
			</li>
		</ul>
	</div>
	<div class="input">
		<label for="secret"><g:message code="webconnection.api.secret.label"/></label>
		<g:textField name='secret' disabled="${!activityInstanceToEdit?.apiEnabled}" value="${activityInstanceToEdit?.secret}"/>
	</div>
</div>
<r:script>
	$("#enableApi").live("change", function() {
		if ($("#enableApi").is(":checked")) {
			$("#secret").removeAttr('disabled');
		}
		else {
			$("#secret").attr('disabled', 'disabled');
		}
	});
</r:script>