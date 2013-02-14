package frontlinesms2

import frontlinesms2.*
import grails.converters.JSON
import org.codehaus.groovy.grails.plugins.PluginManagerHolder

class HelpController extends ControllerUtils {
	def appSettingsService

	def index() { redirect action:'main' }
	
	def main() {}
	
	def section() {
		def helpText
		if(params.helpSection) {
			def suppliedPluginName = params.helpSection.split('/')[0]
			// make sure that this is restricted to configured plugins
			def pluginManager = PluginManagerHolder.pluginManager
			def plugin = pluginManager.allPlugins.find { plugin ->
				suppliedPluginName == 'frontlinesms-core' && plugin.name.startsWith('frontlinesmsCore') || 
						plugin.name == suppliedPluginName
			}
			def pluginDir = plugin.descriptor?.file?.parentFile
			// FIXME this is open to injection attacks
			def markdownFile
			if(pluginDir) {
				markdownFile = new File(pluginDir, "web-app/help/${params.helpSection}.txt")
			} else {
				markdownFile = new File("web-app/help/${params.helpSection}.txt")
			}
			if (markdownFile.canRead()) {
				helpText = markdownFile.text
			}
		}
		if(!helpText) helpText = g.message(code:"help.notfound")
		render text:helpText.markdownToHtml()
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

