<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<r:script library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
		<r:script>
			url_root = "${request.contextPath}/";
			
			function initializePopup() {
				$("#submit").attr('disabled', 'disabled');
			}

			function setActivityTypeSelected() {
				var submit = $("#submit");
				submit.removeAttr('disabled');
				submit.click();
			}
		</r:script>
		<r:layoutResources/>
	</head>
	<body>
		<div class="input select">
			<g:each var="activityType" in="${['announcement', 'autoreply', 'poll']}">
				<label>
					<g:radio name="activity" value="${activityType}" onchange="setActivityTypeSelected()"/>
					<h3><g:message code="${activityType}.label"/></h3>
					<div class="info"><g:message code="${activityType}.description"/></div>
				</label>
			</g:each>
		</div>
		<r:layoutResources/>
	</body>
</html>
