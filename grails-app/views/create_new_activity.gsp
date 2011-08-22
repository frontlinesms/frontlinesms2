<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<g:javascript library="jquery" plugin="jquery"/>
	<jqui:resources theme="medium" plugin="randomtexttosolvebug"/>
	<script type="text/javascript">
		url_root = "${request.contextPath}/";
	</script>
	<g:javascript src="application.js"/>
	<g:javascript src="mediumPopup.js"/>
</head>
<body>
<div id="tabs" class="vertical-tabs">
	<ol>
		<li><a href="#tabs-1">Create new activity or quick message</a></li>
	</ol>

	<div id="tabs-1">
		<ul>
			<li>
				<g:radio name="activity" value="announcement"/>
				<div>Announcements</div>
				<div>Send an announcement message and organise the responses</div>
			</li>
			<li>
				<g:radio name="activity" value="poll"/>
				<div>Poll<</div>
				<div>Send a question and analyse the responses</div>
			</li>
			<li>
				<g:radio name="activity" value="subscription"/>
				<div>Subscription Manager</div>
				<div>Allow people to automatically join and leave contact groups using a message keyword</div>

			</li>
		</ul>
	</div>
</div>
</body>
</html>

<script>
	function addValidations() {
		$("#tabs-1").TabContentWidget({
			validate: function() {
				var selectedElement = $("input[name='activity']:checked")[0]
				if (selectedElement) {
					$("#modalBox").dialog("close");
					remoteHash[selectedElement.value].call()
				}
				return false;
			}
		});
	}
</script>