package frontlinesms2

import frontlinesms2.*
import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.PluginManagerHolder

class HelpController extends ControllerUtils {
	def appSettingsService

	def index() {}
	
	def section() {
		def helpText = this.class.getResource("/help/${params.helpSection}.txt")?.text
		if(!helpText) helpText = g.message(code:"help.notfound")
		render text:helpText.markdownToHtml()
	}

	def image() {
		def uri = r.resource(uri:"/images/${params.imagePath}.png")
		uri = uri.substring(request.requestURI.indexOf('help/images') + 'help/images'.size())
		redirect(uri:uri, absolute:true)
	}

	def updateShowNewFeatures() {
		appSettingsService['newfeatures.popup.show.infuture'] = params.enableNewFeaturesPopup?: false
		appSettingsService.persist()
		render text:[] as JSON
	}

	def newfeatures() {
		appSettingsService['newfeatures.popup.show.immediately'] = false
		appSettingsService.persist()
		params.helpSection = 'frontlinesms-core/features/new'
		section()
	}
}

