package frontlinesms2

import frontlinesms2.*

class HelpController {
	def applicationPropertiesService

	def index() { redirect action:'main' }
	
	def main() {}
	
	def section() {
		def helpText
		if(!params.helpSection) {
			helpText = "This help file is not yet available, sorry."
			render text:helpText.markdownToHtml()
		} else {
			// FIXME this is open to injection attacks
			def markdownFile = new File("web-app/help/" + params.helpSection + ".txt")
			if (markdownFile.canRead()) {
				helpText = markdownFile.text
			} else {
				helpText = "This help file is not yet available, sorry."
			}
			render text:helpText.markdownToHtml()		
		}
	}
	def updateShowNewFeatures(){
		applicationPropertiesService.showNewFeaturesPopup = (params.enableNewFeaturesPopup)?:false
		applicationPropertiesService.lastVersionPopupAlreadyDisplayed = true
		render "success"
	}

	def newfeatures(){
		if(applicationPropertiesService.lastVersionPopupAlreadyDisplayed){
			render text:"last version already displayed"
		} else {
			def markdownFile = new File("web-app/help/core/features/new.txt")
			render template:'/help/newfeatures', model:[newfeatures:markdownFile.text.markdownToHtml()]
		}
	}
}