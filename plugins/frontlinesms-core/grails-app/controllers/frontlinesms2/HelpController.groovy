package frontlinesms2

import frontlinesms2.*
import grails.converters.JSON

class HelpController extends ControllerUtils {
	def appSettingsService

	def index() { redirect action:'main' }
	
	def main() {}
	
	def section() {
		def helpText
		if(params.helpSection) {
			// FIXME this is open to injection attacks
			def markdownFile = new File("web-app/help/" + params.helpSection + ".txt")
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
		params.helpSection = 'core/features/new'
		section()
	}
}

