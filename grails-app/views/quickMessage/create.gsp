<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>
<g:javascript src="characterSMS-count.js"/>
<div id="tabs">

	<div class="error-panel hide">please enter all the details</div>
	<ol>
		<g:each in="['tabs-1' : 'Enter Message', 'tabs-2' : 'Select Recipients',
						'tabs-3' : 'Confirm']" var='entry'>
			<g:if test="${configureTabs.contains(entry.key)}">
				<li><a href="#${entry.key}">${entry.value}</a></li>
			</g:if>
		</g:each>
		<li class="confirm-tab"><a href="#tabs-4"></a></li>
	</ol>

	<g:formRemote name="send-quick-message" url="${[action:'send', controller:'message']}" method="post" onSuccess="goToNextTab()">
		<g:render template="message"/>
		<div id="tabs-2" class="${configureTabs.contains("tabs-2") ? "" : "hide"}">
			<g:render template="select_recipients"/>
		</div>
		<g:render template="confirm"/>
	</g:formRemote>

	<div id="tabs-4" class='quick-message-summary'>
		<h2>The messages  have been added to the pending message queue.</h2>

		<h2>It may take some time for all the messages to be sent, depending on the
		number of messages and the network connection.</h2>

		<h2>To see the status of your message, open the 'Pending' messages folder.</h2>
	</div>

</div>

<script>
	function addTabValidations() {
		$("#tabs").tabs("disable", getTabLength());

		$("#tabs-2").contentWidget({
			validate: function() {
				return isGroupChecked("groups") || isGroupChecked("addresses")
			}
		});

		$("#tabs-3").contentWidget({
			onDone: function() {
				$("#send-quick-message").submit()
				return false;
			}
		});
	}


	function goToNextTab() {
		$("#tabs").tabs("enable", getTabLength());
		$('#tabs').tabs('select', getCurrentTab() + 1);
	}
</script>
