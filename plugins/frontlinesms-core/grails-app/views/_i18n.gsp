var i18nStrings = i18nStrings || {};
function i18n(key) {
	var translated =
		<g:each var="plugin" in="${grailsApplication.config.frontlinesms.plugins}">
			(typeof(i18nStrings["${plugin}"])!=="undefined"? i18nStrings["${plugin}"][key]: null) ||
		</g:each>
			key;
	if(typeof(translated) == 'undefined') return key; // FIXME this line looks unnecessary
	for(i=arguments.length-1; i>0; --i) {
		translated = translated.replace("{"+(i-1)+"}", arguments[i]);
	}
	return translated;
}
