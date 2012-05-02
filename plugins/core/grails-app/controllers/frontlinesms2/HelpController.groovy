package frontlinesms2

class HelpController {
	def index = { redirect(action: main) }
	
	def main = { }
	
	def getSection = {
		def helpText
		if(!params.helpSection){
			helpText = "This help file is not yet available, sorry."
			render text:helpText.markdownToHtml()
		}else{
			def markdownFile = new File("web-app/help/" + params.helpSection + ".txt")
			if (markdownFile.canRead()) {
				helpText = markdownFile.text
			} 
			else {
				helpText = "This help file is not yet available, sorry."
			}
			render text:helpText.markdownToHtml()		
			}
	}
}
