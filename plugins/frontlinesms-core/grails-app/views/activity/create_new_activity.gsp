<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<r:script library="jquery" plugin="jquery"/>
		<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
		<r:script>
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
		<fsms:radioGroup
				name="activity"
				values="${frontlinesms2.Activity.implementations*.shortName}"
				labelSuffix=".label" descriptionSuffix=".description"
				onchange="setActivityTypeSelected()"/>
		<r:layoutResources/>
	</body>
</html>

