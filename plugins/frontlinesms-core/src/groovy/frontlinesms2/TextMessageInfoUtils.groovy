package frontlinesms2
import org.smslib.util.GsmAlphabet

class TextMessageInfoUtils {
	private static final int SMS_LENGTH_LIMIT = 160 
	private static final int SMS_MULTIPART_LENGTH_LIMIT = 153 
	private static final int SMS_LENGTH_LIMIT_UCS2 = 70 
	private static final int SMS_MULTIPART_LENGTH_LIMIT_UCS2 = 63 
	private static final List SMS_DOUBLE_SIZE_CHARACTER_LIST = ['\f', '^', '{', '}', '[', ']', '~', '\\', '|', '€']
	
	public static def getMessageInfos(String text) {
		int charCount = text.size()
		int partCount
		int remaining
		if(charCount == 0) {
			// empty message
			partCount = 1
			remaining = SMS_LENGTH_LIMIT
		} else if(GsmAlphabet.areAllCharactersValidGSM(text)) {
			// 7-bit
			text.each { character ->
				if(character in SMS_DOUBLE_SIZE_CHARACTER_LIST) charCount ++
			}
			if(charCount <= SMS_LENGTH_LIMIT) {
				// single part
				partCount = 1
				remaining = SMS_LENGTH_LIMIT - charCount
			} else {
				// multipart
				partCount = (int) Math.ceil(charCount / SMS_MULTIPART_LENGTH_LIMIT)
				remaining = SMS_MULTIPART_LENGTH_LIMIT - (charCount % SMS_MULTIPART_LENGTH_LIMIT)
				if(remaining == SMS_MULTIPART_LENGTH_LIMIT) remaining = 0
			}
		} else {
			// UCS2
			if(charCount <= SMS_LENGTH_LIMIT_UCS2) {
				// single part
				partCount = 1
				remaining = SMS_LENGTH_LIMIT_UCS2 - charCount
			} else {
				// multipart
				partCount = (int) Math.ceil(charCount / SMS_MULTIPART_LENGTH_LIMIT_UCS2)
				remaining = SMS_MULTIPART_LENGTH_LIMIT_UCS2 - (charCount % SMS_MULTIPART_LENGTH_LIMIT_UCS2)
				if(remaining == SMS_MULTIPART_LENGTH_LIMIT_UCS2) remaining = 0
			}
		}
		[charCount:charCount, partCount:partCount, remaining:remaining]
	}
}
