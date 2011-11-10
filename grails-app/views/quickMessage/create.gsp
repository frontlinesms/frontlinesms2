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

	<g:formRemote name="send-quick-message" url="${[action:'send', controller:'message']}" method="post" onSuccess="goToSummaryTab()">
		<g:render template="message"/>
		<div id="tabs-2" class="${configureTabs.contains("tabs-2") ? "" : "hide"}">
			<g:render template="select_recipients"/>
		</div>
		<g:render template="confirm"/>
		<g:render template="summary"/>
	</g:formRemote>
</div>

<script>
	function initializePopup() {
		$("#tabs").tabs("disable", getTabLength());

		$("#tabs-2").contentWidget({
			validate: function() {
				return isGroupChecked("addresses")
			}
		});
	}
</script>
