package frontlinesms2

import org.smslib.util.GsmAlphabet

class TextMessageInfoService {
	def getMessageInfos(String text) {
		return TextMessageInfoUtils.getMessageInfos(text)
	}
}

