<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
<g:javascript library="jquery" plugin="jquery"/>
<jqui:resources theme="medium" plugin="randomtextosolvebug"/>
<script type="text/javascript">
	url_root = "${request.contextPath}/";
	refresh_rate = ${params.rRate ?: 30000}
</script>
<g:javascript src="jquery.ui.selectmenu.js"/>
<g:if test="${!grails.util.GrailsUtil.environment.equals(org.codehaus.groovy.grails.commons.GrailsApplication.ENV_TEST)}">
	<g:javascript>
		$(function() {
		        // make dropdowns pretty - N.B. this will break geb tests, so should not be done in TEST environment
		        $(".dropdown").selectmenu();
		});
	</g:javascript>
</g:if>

<g:javascript src="application.js"/>
<g:javascript src="mediumPopup.js"/>
<g:javascript src="smallPopup.js"/>
<g:javascript src="pagination.js"/>
<g:render template="/css"/>