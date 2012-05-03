package frontlinesms2

class HelpController {
	def index = { redirect(action: main) }
	
	def main = { }
	
	def section = {
		if(params.helpSection == null){
			def helpText = "This help file is not yet available, sorry."
			render text:helpText.markdownToHtml()
		}else{
			def markdownFile = new File(params.helpSection + ".txt")
			def helpText
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
