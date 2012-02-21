<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
<g:javascript library="jquery" plugin="jquery"/>
<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
<g:javascript>
	url_root = "${request.contextPath}/";
	url = "${request.forwardURI}/";
	refresh_rate = ${params.rRate ?: 30000}
	grailsEnvironment = "${grails.util.GrailsUtil.environment}";
</g:javascript>
<g:if test="${!grails.util.GrailsUtil.environment.equals(org.codehaus.groovy.grails.commons.GrailsApplication.ENV_TEST)}">
	<g:javascript src="jquery.ui.selectmenu.js"/>
	<g:javascript>
		$(function() {
		        // make dropdowns pretty - N.B. this will break geb tests, so should not be done in TEST environment
		        $(".dropdown").selectmenu();
		});
	</g:javascript>
</g:if>
<g:else>
	<g:javascript>
		// declare our own, non-functioning select menu
		$.fn.selectmenu = function() {}
	</g:javascript>
</g:else>

<g:javascript src="application.js"/>
<g:javascript src="mediumPopup.js"/>
<g:javascript src="smallPopup.js"/>
<g:javascript src="pagination.js"/>
<g:render template="/css"/>

