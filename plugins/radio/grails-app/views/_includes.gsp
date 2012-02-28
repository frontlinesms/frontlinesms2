<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico',plugin:'grailsApplication.config.frontlinesms2.plugin')}" type="image/x-icon" />
<r:require module="radio"/>
<r:layoutResources/>
<g:javascript>
	url_root = "${request.contextPath}/";
	url = "${request.forwardURI}/";
	refresh_rate = ${params.rRate ?: 30000}
	grailsEnvironment = "${grails.util.GrailsUtil.environment}";
</g:javascript>
<g:if test="${!grails.util.GrailsUtil.environment.equals(org.codehaus.groovy.grails.commons.GrailsApplication.ENV_TEST)}">
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
