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
			
			function setChecked(activityType) {
				$("#new-activity-choices input[checked=checked]").attr('checked', '');
				$("#new-activity-choices ." + activityType).attr('checked', 'checked');
				$("#submit").removeAttr('disabled');
			}
		</r:script>
		<r:layoutResources/>
	</head>
	<body>
		<form>
			<ul id="new-activity-choices">
				<li class="activity-list-item">
					<input type="radio" name="activity" value="announcement" class="announcement" onclick="setChecked('announcement')" /><span class="activity-choice"><g:message code="announcement.label" /></span>
					<div class="activity-description"><g:message code="announcement.description" /></div>
				</li>
				<li class="activity-list-item">
					<input type="radio" name="activity" value="poll" class="poll" onclick="setChecked('poll')" /><span class="activity-choice"><g:message code="poll.label" /></span>
					<div class="activity-description"><g:message code="poll.description" /></div>
				</li>
				<li class="activity-list-item">
					<input type="radio" name="activity" value="autoreply" class="autoreply" onclick="setChecked('autoreply')" /><span class="activity-choice"><g:message code="autoreply.label" /></span>
					<div class="activity-description"><g:message code="autoreply.description" /></div>
				</li>
			</ul>
		</form>
		<r:layoutResources/>
	</body>
</html>
