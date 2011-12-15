<g:if test="${grailsApplication.config.frontlinesms2.plugin == 'core'}">
	<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'screen.css', plugin:'core')}" media="screen, projection"/>
	<link rel="stylesheet" type="text/css" href="${resource(dir:'jquery-ui/themes/medium',file:'jquery.ui.selectmenu.css', plugin:'core')}" media="screen, projection"/>
	<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'print.css', plugin:'core')}" media="print"/>
	<!--[if lt IE 8]>
		<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'ie.css', plugin:'core')}" media="screen, projection"/>
<![endif]-->
</g:if>
<g:else>
	<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'screen.css', plugin:'radio')}" media="screen, projection"/>
	<link rel="stylesheet" type="text/css" href="${resource(dir:'jquery-ui/themes/medium',file:'jquery.ui.selectmenu.css', plugin:'radio')}" media="screen, projection"/>
	<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'print.css', plugin:'radio')}" media="print"/>
	<!--[if lt IE 8]>
		<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'ie.css', plugin:'radio')}" media="screen, projection"/>
	<![endif]-->
</g:else>
