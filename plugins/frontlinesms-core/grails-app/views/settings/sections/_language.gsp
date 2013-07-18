<div id="language">
	<h2><g:message code="language.label"/></h2>
	<fsms:info message="language.prompt"/>
	<g:form action="selectLocale" method="post">
		<g:select class="dropdown" name="language"
				from="${languageList}"
				optionKey="key" optionValue="value"
				noSelection="[currentLanguage:languageList[currentLanguage]?:'English']"
				onchange="\$(this).parent().submit()" />
	</g:form>
	<div class="clearfix"></div>
</div>