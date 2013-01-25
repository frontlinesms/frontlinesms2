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
		<div class="input">
			<ul class="select radio">
				<g:each var="activityType" in="${frontlinesms2.Activity.implementations*.shortName}">
					<li>
						<label>
							<h3><g:message code="${activityType.toLowerCase()}.label"/></h3>
							<g:radio name="activity" value="${activityType}" onchange="setActivityTypeSelected()"/>
							<fsms:info message="${activityType.toLowerCase()}.description"/>
						</label>
					</li>
				</g:each>
			</ul>
		</div>
		<r:layoutResources/>
	</body>
</html>
