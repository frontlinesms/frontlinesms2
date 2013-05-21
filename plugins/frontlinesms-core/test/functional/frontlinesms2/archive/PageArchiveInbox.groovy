package frontlinesms2.archive
import frontlinesms2.*

class PageArchiveInbox extends PageArchive {
	static def url = "archive"

	String convertToPath(Object[] args) {
		if (!args) {
			return ""
		}
		if (args[0] instanceof Number) {
			 return '/inbox/show/' + args[0]
		} else {
			def activityId = remote { Activity.findByName(args[0])?.id }
			return '/activity/' + activityId + '?messageSection=activity&viewingMessages=true'
		}
	}

	static at = {
		title.endsWith('inbox')
	}
}

