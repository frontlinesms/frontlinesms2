package frontlinesms2

class HelpController {
	def index = { redirect(action: main) }
	
	def main = { }
	
	def getSection = {
		def markdownFile = new File("web-app/help/" + params.helpSection + ".txt")
		println markdownFile.absolutePath
		def helpText
		if (markdownFile.canRead()) {
			helpText = markdownFile.getText()
		} else {
			helpText = "This help file is not yet available, sorry."
		}
		render text:helpText.markdownToHtml()	
	}
}
