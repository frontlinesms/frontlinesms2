package frontlinesms2
import org.smslib.util.GsmAlphabet

class TextMessageInfoUtils {
	/** Maximum number of characters that can be fit into a single 7-bit GSM SMS message. TODO this value should probably be fetched from {@link TpduUtils}. */
	private static final int SMS_LENGTH_LIMIT = 160 
	/** Maximum number of characters that can be fit in one part of a multipart 7-bit GSM SMS message.  TODO this number is incorrect, I suspect.  The value should probably be fetched from {@link TpduUtils}. */
	private static final int SMS_MULTIPART_LENGTH_LIMIT = 135 
	/** Maximum number of characters that can be fit into a single UCS-2 SMS message. TODO this value should probably be fetched from {@link TpduUtils}. */
	private static final int SMS_LENGTH_LIMIT_UCS2 = 70 
	/** Maximum number of characters that can be fit in one part of a multipart UCS-2 SMS message.  TODO this number is incorrect, I suspect.  The value should probably be fetched from {@link TpduUtils}. */
	private static final int SMS_MULTIPART_LENGTH_LIMIT_UCS2 = 60 
	
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
